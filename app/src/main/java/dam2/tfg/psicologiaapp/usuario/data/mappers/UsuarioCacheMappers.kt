package dam2.tfg.psicologiaapp.usuario.data.mappers

import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioEntity
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil

private fun String.aRolUsuario(): RolUsuario = when (this) {
    "PSICOLOGO" -> RolUsuario.PSICOLOGO
    "PACIENTE" -> RolUsuario.PACIENTE
    else -> RolUsuario.SIN_ROL
}

private fun RolUsuario.aStringRol(): String = when (this) {
    RolUsuario.PSICOLOGO -> "PSICOLOGO"
    RolUsuario.PACIENTE -> "PACIENTE"
    RolUsuario.SIN_ROL -> "SIN_ROL"
}

fun UsuarioEntity.toPerfilCacheado(): PerfilCacheado = PerfilCacheado(
    usuarioId = usuarioId,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = fotoPerfilUrl,
    rol = rol.aRolUsuario(),
)

fun UsuarioPerfil.toEntityCache(): UsuarioEntity = UsuarioEntity(
    usuarioId = usuarioId,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = fotoPerfilUrl,
    rol = rol.aStringRol(),
)

