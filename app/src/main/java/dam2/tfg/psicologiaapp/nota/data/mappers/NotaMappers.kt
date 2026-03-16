package dam2.tfg.psicologiaapp.nota.data.mappers

import dam2.tfg.psicologiaapp.nota.data.remote.NotaResponseDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.paciente.data.mappers.toDomain as pacienteToDomain
import dam2.tfg.psicologiaapp.psicologo.data.mappers.toDomain as psicologoToDomain

fun NotaResponseDto.toDomain(): Nota = Nota(
    id = id,
    asunto = asunto,
    descripcion = descripcion,
    paciente = paciente.pacienteToDomain(),
    psicologo = psicologo.psicologoToDomain()
)
