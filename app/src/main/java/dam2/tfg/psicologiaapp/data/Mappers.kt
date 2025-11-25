package dam2.tfg.psicologiaapp.data

import dam2.tfg.psicologiaapp.data.local.entity.UsuarioEntity
import dam2.tfg.psicologiaapp.data.remote.dto.UsuarioDto
import dam2.tfg.psicologiaapp.domain.model.Usuario


fun UsuarioDto.toDomain() = Usuario(
    idBackend = id,
    firebaseUid = firebaseUid,
    email = email,
    nombreUsuario = nombreUsuario
)

fun Usuario.toEntity() = UsuarioEntity(
    firebaseUid = firebaseUid,
    idBackend = idBackend,
    email = email,
    nombreUsuario = nombreUsuario,
)

fun UsuarioEntity.toDomain() = Usuario(
    idBackend = idBackend,
    firebaseUid = firebaseUid,
    email = email,
    nombreUsuario = nombreUsuario
)