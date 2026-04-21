package dam2.tfg.psicologiaapp.presentation.ui.inicio

data class IniciarSesionUiState(
    val correo: String = "",
    val contrasena: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val mostrandoDialogoRecuperacion: Boolean = false,
    val correoRecuperacion: String = "",
    val cargandoRecuperacion: Boolean = false,
    val mensajeErrorRecuperacion: String? = null,
    val mensajeInfoRecuperacion: String? = null,
    val eventoNavegacion: EventoNavegacionIniciarSesion? = null
)

sealed interface EventoNavegacionIniciarSesion {
    data object IrARegistro : EventoNavegacionIniciarSesion
    data object IrAHomePaciente : EventoNavegacionIniciarSesion
    data object IrAHomePsicologo : EventoNavegacionIniciarSesion
}

