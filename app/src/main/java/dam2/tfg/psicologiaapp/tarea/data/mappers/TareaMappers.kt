package dam2.tfg.psicologiaapp.tarea.data.mappers

import dam2.tfg.psicologiaapp.tarea.data.remote.TareaResponseDto
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.paciente.data.mappers.toDomain as pacienteToDomain
import dam2.tfg.psicologiaapp.psicologo.data.mappers.toDomain as psicologoToDomain

fun TareaResponseDto.toDomain(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    horaEnvio = horaEnvio,
    realizada = realizada,
    psicologo = psicologo.psicologoToDomain(),
    paciente = paciente.pacienteToDomain()
)
