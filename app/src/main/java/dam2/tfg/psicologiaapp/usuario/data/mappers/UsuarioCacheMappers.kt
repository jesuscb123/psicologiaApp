package dam2.tfg.psicologiaapp.usuario.data.mappers

import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
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
    psicologoId = psicologoId,
)

fun UsuarioPerfil.toEntityCache(): UsuarioEntity = UsuarioEntity(
    usuarioId = usuarioId,
    firebaseUid = firebaseUid,
    nombre = nombre,
    apellidos = apellidos,
    fotoPerfilUrl = fotoPerfilUrl,
    rol = rol.aStringRol(),
    email = email,
    psicologoId = (this as? PacientePerfil)?.psicologoId,
)

fun UsuarioEntity.toPacientePerfil(): PacientePerfil? {
    if (rol.aRolUsuario() != RolUsuario.PACIENTE) return null
    return PacientePerfil(
        usuarioId = usuarioId,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        psicologoId = psicologoId,
    )
}

fun UsuarioEntity.toPsicologoPerfil(): PsicologoPerfil? {
    if (rol.aRolUsuario() != RolUsuario.PSICOLOGO) return null
    return PsicologoPerfil(
        usuarioId = usuarioId,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        numeroColegiado = "",
        especialidades = emptyList(),
    )
}

