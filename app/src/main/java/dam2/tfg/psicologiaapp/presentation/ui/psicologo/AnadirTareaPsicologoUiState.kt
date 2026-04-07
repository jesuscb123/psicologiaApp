package dam2.tfg.psicologiaapp.presentation.ui.psicologo

data class AnadirTareaPsicologoUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val eventoNavegacion: EventoNavegacionAnadirTareaPsicologo? = null,
) {
    val esFormularioValido: Boolean
        get() = titulo.trim().isNotBlank() && descripcion.trim().isNotBlank()
}

sealed interface EventoNavegacionAnadirTareaPsicologo {
    data object TareaGuardada : EventoNavegacionAnadirTareaPsicologo
}
