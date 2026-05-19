package dam2.tfg.psicologiaapp.paciente.data.mappers

import dam2.tfg.psicologiaapp.paciente.data.local.PacienteEntity
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente

fun PacienteResponseDto.toDomain(): Paciente = Paciente(
    usuarioId = id,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = normalizarUrlFotoPerfilCliente(fotoPerfilUrl),
    psicologoId = psicologoId,
    idPaciente = idPaciente
)

fun PacienteResponseDto.toEntity(): PacienteEntity = PacienteEntity(
    idPaciente = idPaciente,
    usuarioId = id,
    psicologoId = psicologoId,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = normalizarUrlFotoPerfilCliente(fotoPerfilUrl),
)

fun PacienteEntity.toDomain(): Paciente = Paciente(
    usuarioId = usuarioId,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = fotoPerfilUrl,
    psicologoId = psicologoId,
    idPaciente = idPaciente,
)

