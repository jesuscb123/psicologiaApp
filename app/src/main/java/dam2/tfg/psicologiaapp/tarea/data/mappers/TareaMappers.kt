package dam2.tfg.psicologiaapp.tarea.data.mappers

import dam2.tfg.psicologiaapp.tarea.data.local.TareaEntity
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaResponseDto
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

fun TareaResponseDto.toDomain(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    horaEnvio = horaEnvio,
    realizada = realizada,
    aceptadaPorPaciente = aceptadaPorPaciente ?: false,
    psicologoId = psicologo.idEntidadPsicologo,
    pacienteId = paciente.idPaciente,
)

fun TareaResponseDto.toEntity(): TareaEntity = TareaEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    horaEnvio = horaEnvio,
    realizada = realizada,
    aceptadaPorPaciente = aceptadaPorPaciente ?: false,
    psicologoId = psicologo.idEntidadPsicologo,
    pacienteId = paciente.idPaciente,
)

fun TareaEntity.toDomain(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    horaEnvio = horaEnvio,
    realizada = realizada,
    aceptadaPorPaciente = aceptadaPorPaciente,
    psicologoId = psicologoId,
    pacienteId = pacienteId,
)
