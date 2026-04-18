package dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas

data class MisCitasPsicologoUiState(
    val cargando: Boolean = false,
    val filtroSeleccionado: FiltroMisCitas = FiltroMisCitas.ACTIVAS,
    val citas: List<Cita> = emptyList(),
    val mensajeError: String? = null,
)

