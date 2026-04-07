package dam2.tfg.psicologiaapp.tarea.data.remote

import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto

/**
 * DTOs remotos de tareas, alineados con los contratos del backend.
 */

data class TareaCrearRequestDto(
    val titulo: String,
    val descripcion: String
)

data class TareaActualizarRequestDto(
    val titulo: String,
    val descripcion: String,
    val realizada: Boolean
)

data class TareaActualizarRealizadaRequestDto(
    val realizada: Boolean
)

data class TareaResponseDto(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val horaEnvio: String,
    val realizada: Boolean,
    val aceptadaPorPaciente: Boolean? = null,
    val psicologo: PsicologoResponseDto,
    val paciente: PacienteResponseDto
)
