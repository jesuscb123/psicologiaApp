package dam2.tfg.psicologiaapp.presentation.ui.paciente

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

data class PerfilPsicologoUiState(
    val cargando: Boolean = false,
    val asignando: Boolean = false,
    val psicologo: Psicologo? = null,
    val mensajeError: String? = null,
    val eventoNavegacion: EventoNavegacionPerfilPsicologo? = null
)

sealed interface EventoNavegacionPerfilPsicologo {
    data object AsignacionCompletada : EventoNavegacionPerfilPsicologo
}

