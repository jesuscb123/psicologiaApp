package dam2.tfg.psicologiaapp.presentation.ui.registro

data class SeleccionRolRegistroUiState(
    val eventoNavegacion: EventoNavegacionSeleccionRol? = null
)

sealed interface EventoNavegacionSeleccionRol {
    data object IrARegistroPaciente : EventoNavegacionSeleccionRol
    data object IrARegistroPsicologo : EventoNavegacionSeleccionRol
    data object Volver : EventoNavegacionSeleccionRol
}

