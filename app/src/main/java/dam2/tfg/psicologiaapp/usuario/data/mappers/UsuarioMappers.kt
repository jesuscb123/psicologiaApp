package dam2.tfg.psicologiaapp.usuario.data.mappers

import dam2.tfg.psicologiaapp.paciente.data.remote.PacientePerfilResponseDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoPerfilResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto
import dam2.tfg.psicologiaapp.paciente.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.paciente.domain.model.UsuarioPaciente
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.psicologo.domain.model.UsuarioPsicologo
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioSinRol

private fun String.aRolUsuario(): RolUsuario = when (this) {
    "PSICOLOGO" -> RolUsuario.PSICOLOGO
    "PACIENTE" -> RolUsuario.PACIENTE
    else -> RolUsuario.SIN_ROL
}

fun UsuarioResponseDto.toDomain(): Usuario = when (this) {
    is PsicologoResponseDto -> UsuarioPsicologo(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        numeroColegiado = numeroColegiado,
        especialidad = especialidad,
        descripcion = descripcion,
    )
    is PacienteResponseDto -> UsuarioPaciente(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        psicologoId = psicologoId,
        idPaciente = idPaciente
    )
    else -> UsuarioSinRol(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario()
    )
}

fun UsuarioPerfilResponseDto.toDomain(): UsuarioPerfil = when (this) {
    is PsicologoPerfilResponseDto -> PsicologoPerfil(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        numeroColegiado = numeroColegiado,
        especialidad = especialidad,
        descripcion = descripcion,
    )
    is PacientePerfilResponseDto -> PacientePerfil(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        psicologoId = psicologoId
    )
    else -> UsuarioPerfilBasico(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario()
    )
}

fun UsuarioRequest.toDto(): UsuarioRequestDto = when (this) {
    is PsicologoRequest -> PsicologoRequestDto(
        nombre = nombre,
        apellidos = apellidos,
        fotoPerfilUrl = fotoPerfilUrl,
        numeroColegiado = numeroColegiado,
        especialidad = especialidad,
        descripcion = descripcion,
    )
    is PacienteRequest -> PacienteRequestDto(
        nombre = nombre,
        apellidos = apellidos,
        fotoPerfilUrl = fotoPerfilUrl,
        psicologoId = psicologoId
    )
    else -> error("Tipo de UsuarioRequest no soportado: ${this::class.qualifiedName}")
}
