package dam2.tfg.psicologiaapp.psicologo.data.mappers

import dam2.tfg.psicologiaapp.paciente.data.mappers.normalizarUrlFotoPerfilCliente
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoEntity
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

fun PsicologoResponseDto.toDomain(): Psicologo = Psicologo(
    usuarioId = id,
    idEntidadPsicologo = idEntidadPsicologo,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = normalizarUrlFotoPerfilCliente(fotoPerfilUrl),
    numeroColegiado = numeroColegiado,
    especialidades = especialidades,
    descripcion = descripcion,
)

fun PsicologoResponseDto.toEntity(): PsicologoEntity = PsicologoEntity(
    usuarioId = id,
    idEntidadPsicologo = idEntidadPsicologo,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = normalizarUrlFotoPerfilCliente(fotoPerfilUrl),
    numeroColegiado = numeroColegiado,
    especialidades = especialidades,
    descripcion = descripcion,
)

fun PsicologoEntity.toDomain(): Psicologo = Psicologo(
    usuarioId = usuarioId,
    idEntidadPsicologo = idEntidadPsicologo,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = normalizarUrlFotoPerfilCliente(fotoPerfilUrl),
    numeroColegiado = numeroColegiado,
    especialidades = especialidades,
    descripcion = descripcion,
)
