package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import java.time.LocalDate
import java.time.ZoneId

data class CitasMenuUiState(
    val citas: List<Cita> = emptyList(),
    val proximaCita: Cita? = null,
    val historialPreview: List<Cita> = emptyList(),
    val diasReservaRapida: List<LocalDate> = emptyList(),
    val fechaReservaRapidaSeleccionada: LocalDate? = null,
    val disponibilidad: DisponibilidadDia? = null,
    val zonaHoraria: String = ZoneId.systemDefault().id,
    val cargandoCitas: Boolean = false,
    val cargandoDisponibilidad: Boolean = false,
    val cargandoReserva: Boolean = false,
    val mensajeError: String? = null,
)
