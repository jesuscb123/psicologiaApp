package dam2.tfg.psicologiaapp.cita.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class DisponibilidadDia(
    val fecha: LocalDate,
    val zonaHoraria: String,
    val horasDisponibles: List<LocalTime> = emptyList(),
)

