package dam2.tfg.psicologiaapp.cita.data.remote

import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto

data class CitaCrearRequestDto(
    /** Instante de inicio en formato ISO-8601 con offset (p. ej. "2026-04-15T09:00:00+02:00"). */
    val inicio: String,
    /** Zona horaria local IANA del usuario (p. ej. "Europe/Madrid"). */
    val zonaHoraria: String,
)

data class DisponibilidadResponseDto(
    /** Fecha en formato ISO (YYYY-MM-DD) en la zona horaria solicitada. */
    val fecha: String,
    val zonaHoraria: String,
    /** Horas disponibles como LocalTime ISO (p. ej. "09:00" o "09:00:00"). */
    val horasDisponibles: List<String> = emptyList(),
)

data class CitaResponseDto(
    val id: Long,
    /** ISO-8601 con offset. */
    val inicio: String,
    /** ISO-8601 con offset. */
    val fin: String,
    val psicologo: PsicologoResponseDto,
    val paciente: PacienteResponseDto,
    val estadoPersistido: String,
    val estadoCalculado: String,
)

