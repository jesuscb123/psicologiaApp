package dam2.tfg.psicologiaapp.presentation.ui.paciente.perfilPsicologo

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

data class PerfilPsicologoUiState(
    val cargando: Boolean = false,
    val asignando: Boolean = false,
    val psicologo: Psicologo? = null,
    val mensajeError: String? = null,
    val eventoNavegacion: EventoNavegacionPerfilPsicologo? = null,
    val pacienteYaTienePsicologo: Boolean = false,
)

sealed interface EventoNavegacionPerfilPsicologo {
    data object AsignacionCompletada : EventoNavegacionPerfilPsicologo
}
