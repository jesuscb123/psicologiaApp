# APUNTES (frontend — Android)

## 2026-04-06 — Cambios en `psicologiaapp`

### Flujo psicólogo (navegación y datos)

- **204 en listas vacías**: APIs que devuelven `List` con **204**; `PsicologoApi`, `NotaApi` y `TareaApi` usan `Response<List<...>>` y los repositorios mapean 204 / cuerpo nulo a `emptyList()`.
- **Menú lateral**: `MenuLateralPerfilViewModel` admite también rol **PSICOLOGO** (nombre y foto como en paciente).
- **Rutas**: `Grafo_PSICOLOGO`, `RutasGrafoPsicologo` (home, ficha paciente, añadir tarea, ajustes, acerca de).
- **Home psicólogo**: `HomePsicologoViewModel` / `PantallaHomePsicologo`, grid de pacientes con **`TarjetaPacienteApp`** (inicialmente avatar con iniciales; ver más abajo).
- **Ficha paciente** (`PantallaFichaPacientePsicologo`): pestañas notas/tareas, `ListaNotasApp(permitirEliminar = false)`, `ListaTareasApp`, FAB a **añadir tarea** cuando la pestaña es tareas.

### Pantalla «añadir tarea» (psicólogo)

- `AnadirTareaPsicologoViewModel` / `PantallaAnadirTareaPsicologo` con **`CrearTareaUseCase`**; navegación desde el FAB de la ficha.

### Fotos de perfil del paciente (vista psicólogo)

- **`TarjetaPacienteApp`**: `AvatarPerfilCircularApp` con `fotoPerfilUrl` del dominio.
- **`BarraSuperiorApp`**: parámetros opcionales para **avatar junto al título** (`mostrarAvatarJuntoTitulo`, etc.).
- **`FichaPacientePsicologo`**: estado con `fotoPerfilUrlPaciente`; carga desde la lista de pacientes del psicólogo.
- **`UrlFotoPerfilCliente`** + **`PacienteMappers`**: normalizar URLs con `localhost` hacia `BuildConfig.BASE_URL` (coherente con perfil de usuario).

### Tareas y estados (paciente y psicólogo)

- **Dominio** `Tarea`: `aceptadaPorPaciente`; DTO `TareaResponseDto` con campo opcional para compatibilidad.
- **`TareaApi`**: `PATCH .../aceptada`; repositorio y **`AceptarTareaUseCase`**.
- **`HomePacienteViewModel`**: carga **notas y tareas** (con psicólogo asignado); `aceptarTarea` y `marcarTareaRealizada`.
- **`HomePacienteScreen`**: sección **«Tus tareas»**; **`ListaTareasPacienteApp`** (tarjetas pulsables); **`AlertDialog`** con título/descripción, **Aceptar**, **Marcar como completada** o **Cerrar** según estado.
- **`ListaNotasApp`**: modo **`listaPlana`** para usar scroll único en el home con notas + tareas.
- **`ListaTareasApp`** (psicólogo): texto de estado **`textoEstadoTarea()`** — «Pendiente de aceptación», «Aceptada», «Completada».
- **`TextoEstadoTarea.kt`**: función compartida para el texto de estado.

### Archivos tocados (referencia rápida)

- Navegación: `RutasApp`, `AppNavHost`, `GrafoPsicologoNavegacion`.
- UI psicólogo: `presentation/ui/psicologo/*` (home, ficha, añadir tarea, estados).
- Componentes: `TarjetaPacienteApp`, `ListaTareasApp`, `ListaTareasPacienteApp`, `ListaNotasApp`, `BarraSuperiorApp`, `TextoEstadoTarea`.
- Datos tareas: `tarea/data/*`, `tarea/domain/*`, use cases `AceptarTareaUseCase`, `GetTareasPacienteActualUseCase`, `MarcarTareaRealizadaUseCase`.
- Paciente: `paciente/data/mappers/UrlFotoPerfilCliente.kt`, `PacienteMappers`.

> Nota: el backend debe exponer los nuevos campos y endpoints de tareas (`aceptadaPorPaciente`, `PATCH /aceptada`) para que el flujo completo funcione en producción.
