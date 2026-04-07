package dam2.tfg.psicologiaapp.paciente.data.mappers

import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente

fun PacienteResponseDto.toDomain(): Paciente = Paciente(
    usuarioId = id,
    firebaseUid = firebaseUid,
    nombreUsuario = nombreUsuario,
    fotoPerfilUrl = normalizarUrlFotoPerfilCliente(fotoPerfilUrl),
    psicologoId = psicologoId,
    idPaciente = idPaciente
)

