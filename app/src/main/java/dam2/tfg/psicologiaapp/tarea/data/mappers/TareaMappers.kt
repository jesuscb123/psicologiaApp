package dam2.tfg.psicologiaapp.tarea.data.mappers

import dam2.tfg.psicologiaapp.tarea.data.remote.TareaResponseDto
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDomain

fun TareaResponseDto.toDomain(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    horaEnvio = horaEnvio,
    realizada = realizada,
    psicologo = psicologo.toDomain(),
    paciente = paciente.toDomain()
)
