# Apuntes de implementación — home paciente (notas)

**Fecha:** 3 de abril de 2026  

---

## Contexto y objetivo

En el home del paciente, las notas se cargan desde la API (Retrofit). Ya existía el endpoint y el caso de uso `BorrarNotaUseCase` para eliminar una nota en el servidor, pero la UI no permitía borrarlas de forma intuitiva ni con confirmación.

**Objetivo:** permitir **eliminar una nota** deslizando la tarjeta (solo en una dirección), mostrar un **diálogo de confirmación** y, si el usuario confirma, llamar al backend y **actualizar la lista en memoria** sin recargar todo el perfil.

---

## Cómo se ha realizado (arquitectura)

Se ha respetado la arquitectura MVVM del proyecto:

| Capa | Cambio |
|------|--------|
| **Domain** | Sin cambios: se reutiliza `BorrarNotaUseCase` (ya delegaba en `NotaRepository`). |
| **Presentation — ViewModel** | `HomePacienteViewModel` recibe `BorrarNotaUseCase` por Hilt y expone `eliminarNota(notaId: Long)`. |
| **Presentation — UI** | `ListaNotasApp` añade gesto `SwipeToDismissBox` y callback `alSolicitarEliminar`. `PantallaHomePaciente` guarda la nota pendiente y muestra `AlertDialog`. |

No se pasa `NavController` a componentes de lista: el flujo es **callback hacia la pantalla** → estado local → diálogo → ViewModel.

---

## Qué se ha cambiado y por qué

### 1. `HomePacienteViewModel.kt`

- **Qué:** inyección de `BorrarNotaUseCase` y función `eliminarNota(notaId: Long)`.
- **Por qué:** la lógica de borrado debe vivir en el ViewModel usando el caso de uso, no en Composables ni llamadas directas al repositorio.
- **Comportamiento:** tras `Result` exitoso, se filtra la nota en `uiState.notas` para reflejar el borrado al instante; ante error, se actualiza `mensajeError` para informar al usuario.

### 2. `ListaNotasApp.kt`

- **Qué:** cada ítem va dentro de `SwipeToDismissBox` (Material 3), con `items(notas, key = { it.id })` para estabilidad en listas.
- **Por qué:** feedback visual claro (fondo “Eliminar”) y una sola dirección de gesto evita confusiones.
- **Detalle importante:** `enableDismissFromStartToEnd = true` y `enableDismissFromEndToStart = false` (solo deslizamiento en la dirección acordada).
- **`confirmValueChange`:** cuando el gesto llegaría a completar el dismiss en dirección `StartToEnd`, se invoca `alSolicitarEliminar(nota)` y se devuelve **`false`** para que la tarjeta **no desaparezca sola**; así el borrado real queda condicionado al diálogo.

### 3. `HomePacienteScreen.kt` (`PantallaHomePaciente`)

- **Qué:** estado `notaPendienteEliminar` con `remember` / `mutableStateOf`, `AlertDialog` en español (cancelar / eliminar) y enlace `alSolicitarEliminar = { notaPendienteEliminar = it }`.
- **Por qué:** evitar borrados accidentales y cumplir el patrón de UI del proyecto (sin lógica de negocio en el composable más allá de orquestar estado y llamadas al ViewModel).

---

## Problema: la lista no se actualizaba al añadir una nota

### Síntoma

Tras guardar una nota nueva en `PantallaAnadirNota` y volver al home con `popBackStack()`, la nota **no aparecía** en la lista hasta cerrar y volver a abrir la aplicación. El borrado con swipe sí reflejaba los cambios al instante.

### Causas que se corrigieron

1. **Patrón con `SavedStateHandle` + `getStateFlow("recargar_notas")` en el ViewModel**  
   La idea era marcar `recargar_notas = true` en la entrada de navegación del home antes de hacer `popBackStack()`. En la práctica, el `SavedStateHandle` que observaba el `collect` en el ViewModel **no coincidía** de forma fiable con el que actualizaba la navegación (p. ej. al usar `hiltViewModel()` sin anclarlo explícitamente al `NavBackStackEntry` del home), así que el flujo de recarga **no se disparaba** al volver.

2. **Condición de carrera entre dos `recargar()`**  
   `recargar()` lanza trabajo asíncrono (perfil, psicólogos, notas). Si al volver del alta se iniciaba una **nueva** recarga mientras la **anterior** seguía en curso, la respuesta **vieja** podía aplicarse **después** que la nueva y dejar la lista **sin** la nota recién creada, aunque el servidor ya la tuviera.

### Solución aplicada

- **Recarga al recomponer el home:** en `PantallaHomePaciente` se llama a `viewModel.recargar()` dentro de `LaunchedEffect(Unit)`. Al navegar a otra ruta, el composable del home se sale de composición y ese efecto se cancela; al volver con `popBackStack()`, el home se vuelve a componer y **`LaunchedEffect` se ejecuta de nuevo**, disparando una recarga sin depender de `SavedStateHandle` ni de banderas en la navegación.

- **Una sola recarga coherente:** en `HomePacienteViewModel`, la recarga guarda su `Job` en `trabajoRecarga`, **cancela** el trabajo anterior al iniciar otro `recargar()`, y usa `ensureActive()` tras operaciones suspendidas para no aplicar estado si esa corrutina ya fue cancelada.

- **`AppNavHost`:** en `alNotaGuardada` basta con `navController.popBackStack()`; no hace falta escribir claves en `SavedStateHandle` para este flujo.

---

## Resumen técnico del flujo

1. El usuario desliza la tarjeta en la dirección permitida hasta el umbral.
2. Se muestra el fondo de “Eliminar” y, al intentar completar el dismiss, se abre el diálogo (la tarjeta vuelve a su sitio).
3. Si confirma, `viewModel.eliminarNota(nota.id)` ejecuta `BorrarNotaUseCase` → API `DELETE` según contrato existente.
4. Si la API responde bien, la nota se quita de la lista en `UiState`.

---

## Pruebas sugeridas

- Variante `localDebug` o `prodDebug` con backend accesible (ver `PRUEBAS_APP.md`).
- Con al menos una nota: deslizar → cancelar (no debe borrarse).
- Deslizar → eliminar → comprobar que desaparece y que un error de red muestra mensaje en pantalla.

---

## Archivos tocados (referencia rápida)

- `app/src/main/java/dam2/tfg/psicologiaapp/presentation/ui/paciente/HomePacienteViewModel.kt` (borrado con swipe, `recargar()` con cancelación de job y `ensureActive`)
- `app/src/main/java/dam2/tfg/psicologiaapp/presentation/components/ListaNotasApp.kt`
- `app/src/main/java/dam2/tfg/psicologiaapp/presentation/ui/paciente/HomePacienteScreen.kt` (`LaunchedEffect` → `recargar()` al entrar en el home)
- `app/src/main/java/dam2/tfg/psicologiaapp/presentation/navegacion/AppNavHost.kt` (vuelta desde añadir nota solo con `popBackStack()`)

Casos de uso: `BorrarNotaUseCase`; para la lista tras crear nota se reutiliza la misma `recargar()` que ya llama a `GetNotasPacienteActualUseCase`.

---

# Foto de perfil (paciente) y Firebase Storage

**Fecha:** 4 de abril de 2026  

---

## Por qué Firebase Storage si Firebase solo se usa para autenticación

El backend guarda la foto como **URL en texto** (`fotoPerfilUrl`), no como fichero en el servidor Spring. Para obtener esa URL hace falta **subir los bytes de la imagen a algún almacenamiento** y luego persistir la cadena con `PATCH /api/usuarios/me/foto`.

**Firebase Auth** solo cubre identidad y tokens; **no aloja imágenes** ni genera URLs de ficheros. Por eso el flujo de la app usa **Firebase Storage** como almacén de objetos dentro del **mismo proyecto Firebase** que ya tiene Auth: es la pieza estándar para “subir archivo → URL de descarga → enviar URL al API”. No implica usar Firebase para lógica de negocio adicional; es almacenamiento de ficheros acoplado al usuario autenticado (reglas con `request.auth`).

**Alternativas** si se quisiera evitar Storage por completo: subida `multipart` al propio backend (o a S3, etc.) y que el servidor devuelva o guarde la URL pública. Eso es más trabajo en Spring y despliegue.

---

## Flujo resumido en la app

1. El usuario abre el menú lateral y pulsa el avatar → selector de galería (`PickVisualMedia`).
2. Se leen los bytes del `Uri` (con límite de tamaño, p. ej. 5 MB).
3. **Data:** `UsuarioRepositoryImpl` envía `multipart/form-data` a `POST api/usuarios/me/foto` (parte `archivo`); el backend guarda el fichero y devuelve el perfil con `fotoPerfilUrl` pública.
4. **Domain:** `SincronizarFotoPerfilUseCase` delega en `UsuarioRepository.subirFotoPerfil`.
5. **Presentation:** `MenuLateralPerfilViewModel` actualiza `fotoPerfilUrl` (y nombre) en el estado del menú; la barra superior del grafo paciente lee ese mismo estado.

Archivos de referencia: `UsuarioRepositoryImpl`, `SincronizarFotoPerfilUseCase`, `MenuLateralPerfilViewModel`, `GrafoPacienteNavegacion`, `UsuarioApi`.

---

## Qué configurar en Firebase

- Activar **Cloud Storage** en el proyecto y definir **reglas** que permitan a usuarios autenticados escribir (y leer según política) en la ruta acordada, p. ej. bajo `perfiles/{userId}/`. Sin reglas adecuadas la subida fallará en tiempo de ejecución aunque Auth funcione.

---

## Firebase App Check y subida a Storage (debug / release)

**Contexto:** Si en Logcat aparece `No AppCheckProvider installed` y un token placeholder, o errores de `StorageException` con **404** durante `beginResumableUpload` (“Object does not exist”, “The server has terminated the upload session”), suele estar relacionado con **App Check**: el SDK de Storage intenta adjuntar un token de integridad y, si en la consola tienes **App Check aplicado a Cloud Storage** sin un proveedor válido en la app, las peticiones pueden fallar.

**En el código:** La clase `PsicologiaApp` instala App Check al arrancar:

- Builds **debug** (`BuildConfig.DEBUG == true`): `DebugAppCheckProviderFactory` (desarrollo desde Android Studio / APK de depuración).
- Builds **release**: `PlayIntegrityAppCheckProviderFactory` (dispositivos reales; exige configuración coherente con Play Integrity si publicas en Play).

Dependencias: `firebase-appcheck-debug` y `firebase-appcheck-playintegrity` (versiones alineadas con el BOM de Firebase en `libs.versions.toml` / `app/build.gradle.kts`).

### Registrar el token de depuración (obligatorio para debug si App Check está activo)

1. En **Firebase Console** → **Build** → **App Check**, selecciona la app Android del proyecto.
2. Instala y ejecuta un build **debug** de esta app en un dispositivo o emulador.
3. Abre **Logcat** y filtra por `DebugAppCheckProvider` o `AppCheck` (o busca texto tipo “debug secret” / token que imprime Firebase la primera vez).
4. En App Check → **Manage debug tokens** (gestionar tokens de depuración) → **Add debug token** y pega el token que muestra el log.
5. Vuelve a probar la subida de foto de perfil.

Sin ese token registrado, un modo **Enforced** sobre Storage seguirá rechazando peticiones desde depuración aunque el código instale el proveedor de debug.

### Si no quieres usar App Check todavía

Puedes dejar Storage **sin aplicación estricta** de App Check en la consola (solo monitorización o sin forzar). El aviso del placeholder puede seguir apareciendo en el log, pero las subidas a veces funcionan; con **Enforced** sin proveedor ni token de debug, fallará de forma predecible.

### Otros mensajes del log (referencia)

- `PHASE_CLIENT_ALREADY_HIDDEN` al cerrar el selector de imágenes: suele ser ruido del ciclo de vida del photo picker, no el diagnóstico principal de Storage.
- Líneas de **MIUI** / `ActivityManagerWrapper` / lista de tareas recientes: salen del launcher del fabricante (p. ej. Xiaomi), no indican un fallo en la lógica de la app.
