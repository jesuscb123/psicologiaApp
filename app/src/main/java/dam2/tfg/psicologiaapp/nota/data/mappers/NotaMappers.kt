package dam2.tfg.psicologiaapp.nota.data.mappers

import dam2.tfg.psicologiaapp.nota.data.remote.NotaResponseDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDomain

fun NotaResponseDto.toDomain(): Nota = Nota(
    id = id,
    asunto = asunto,
    descripcion = descripcion,
    paciente = paciente.toDomain(),
    psicologo = psicologo.toDomain()
)
