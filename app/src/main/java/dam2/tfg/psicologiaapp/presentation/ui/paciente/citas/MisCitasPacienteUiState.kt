package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import dam2.tfg.psicologiaapp.cita.domain.model.Cita

data class MisCitasPacienteUiState(
    val cargando: Boolean = false,
    val citas: List<Cita> = emptyList(),
    val mensajeError: String? = null,
)

