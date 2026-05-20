package dam2.tfg.psicologiaapp.presentation.ui.paciente.anadirNota

data class AnadirNotaUiState(
    val asunto: String = "",
    val descripcion: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val eventoNavegacion: EventoNavegacionAnadirNota? = null
) {
    val esFormularioValido: Boolean
        get() = asunto.trim().isNotBlank() && descripcion.trim().isNotBlank()
}

sealed interface EventoNavegacionAnadirNota {
    data object NotaGuardada : EventoNavegacionAnadirNota
}
