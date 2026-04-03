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
