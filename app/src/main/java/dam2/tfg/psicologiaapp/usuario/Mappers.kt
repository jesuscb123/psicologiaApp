package dam2.tfg.psicologiaapp.usuario

import dam2.tfg.psicologiaapp.usuario.data.local.entity.UsuarioEntity
import dam2.tfg.psicologiaapp.usuario.data.dto.UsuarioDto
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario


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