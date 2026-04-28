package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente

data class HomePsicologoUiState(
    val cargando: Boolean = false,
    val nombreUsuarioPsicologo: String = "",
    val listaPacientes: List<Paciente> = emptyList(),
    val mapaCitaProxima: Map<Long, Cita?> = emptyMap(),
    val mensajeError: String? = null,
)
