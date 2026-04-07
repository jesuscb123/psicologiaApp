package dam2.tfg.psicologiaapp.nota.data.mappers

import dam2.tfg.psicologiaapp.nota.data.local.NotaEntity
import dam2.tfg.psicologiaapp.nota.data.remote.NotaResponseDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota

fun NotaResponseDto.toDomain(): Nota = Nota(
    id = id,
    asunto = asunto,
    descripcion = descripcion,
    pacienteId = paciente.idPaciente,
    psicologoId = psicologo.idEntidadPsicologo,
)

fun NotaResponseDto.toEntity(): NotaEntity = NotaEntity(
    id = id,
    asunto = asunto,
    descripcion = descripcion,
    pacienteId = paciente.idPaciente,
    psicologoId = psicologo.idEntidadPsicologo,
)

fun NotaEntity.toDomain(): Nota = Nota(
    id = id,
    asunto = asunto,
    descripcion = descripcion,
    pacienteId = pacienteId,
    psicologoId = psicologoId,
)
