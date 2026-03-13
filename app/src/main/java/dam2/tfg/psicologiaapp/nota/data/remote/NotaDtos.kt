package dam2.tfg.psicologiaapp.nota.data.remote

import dam2.tfg.psicologiaapp.usuario.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.PsicologoResponseDto

/**
 * DTOs remotos de notas, alineados con los contratos del backend.
 */

data class NotaRequestDto(
    val asunto: String,
    val descripcion: String
)

data class NotaResponseDto(
    val id: Long,
    val asunto: String,
    val descripcion: String,
    val paciente: PacienteResponseDto,
    val psicologo: PsicologoResponseDto
)
