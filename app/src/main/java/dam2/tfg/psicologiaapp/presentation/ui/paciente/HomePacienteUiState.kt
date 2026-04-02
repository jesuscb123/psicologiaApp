package dam2.tfg.psicologiaapp.presentation.ui.paciente

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

data class HomePacienteUiState(
    val cargando: Boolean = false,
    val perfilPaciente: PacientePerfil? = null,
    val listaPsicologos: List<Psicologo> = emptyList(),
    val psicologoAsignado: Psicologo? = null,
    val notas: List<Nota> = emptyList(),
    val mensajeError: String? = null
)

