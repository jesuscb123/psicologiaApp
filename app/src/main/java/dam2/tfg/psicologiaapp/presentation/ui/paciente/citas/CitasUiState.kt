package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import java.time.LocalDate
import java.time.LocalTime

data class CitasUiState(
    val fechaSeleccionada: LocalDate = LocalDate.now(),
    val zonaHoraria: String = "",
    val disponibilidad: DisponibilidadDia? = null,
    val horaSeleccionada: LocalTime? = null,
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val eventoNavegacion: EventoNavegacionCitas? = null,
)

sealed interface EventoNavegacionCitas {
    data object CitaReservada : EventoNavegacionCitas
}

