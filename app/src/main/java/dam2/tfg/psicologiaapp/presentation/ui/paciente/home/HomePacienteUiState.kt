package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

data class HomePacienteUiState(
    val cargando: Boolean = false,
    val perfilPaciente: PacientePerfil? = null,
    val listaPsicologos: List<Psicologo> = emptyList(),
    val psicologoAsignado: Psicologo? = null,
    val notas: List<Nota> = emptyList(),
    val tareas: List<Tarea> = emptyList(),
    val proximaCita: Cita? = null,
    val cargandoProximaCita: Boolean = true,
    val mensajeError: String? = null,
    val tieneMensajeNoLeido: Boolean = false,
)
