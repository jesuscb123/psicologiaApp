package dam2.tfg.psicologiaapp.psicologo.data.mappers

import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

fun PsicologoResponseDto.toDomain(): Psicologo = Psicologo(
    usuarioId = id,
    idEntidadPsicologo = idEntidadPsicologo,
    firebaseUid = firebaseUid,
    nombreUsuario = nombreUsuario,
    fotoPerfilUrl = fotoPerfilUrl,
    numeroColegiado = numeroColegiado,
    especialidad = especialidad
)

