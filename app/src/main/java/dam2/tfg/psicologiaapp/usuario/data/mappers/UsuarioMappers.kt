package dam2.tfg.psicologiaapp.usuario.data.mappers

import dam2.tfg.psicologiaapp.usuario.data.remote.PacientePerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.PacienteRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.PsicologoPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.PsicologoRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto
import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import dam2.tfg.psicologiaapp.usuario.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.PsicologoRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.Psicologo
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPaciente
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPsicologo
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioSinRol

private fun String.aRolUsuario(): RolUsuario = when (this) {
    "PSICOLOGO" -> RolUsuario.PSICOLOGO
    "PACIENTE" -> RolUsuario.PACIENTE
    else -> RolUsuario.SIN_ROL
}

fun PsicologoResponseDto.toDomain(): Psicologo = Psicologo(
    usuarioId = id,
    firebaseUid = firebaseUid,
    nombreUsuario = nombreUsuario,
    fotoPerfilUrl = fotoPerfilUrl,
    numeroColegiado = numeroColegiado,
    especialidad = especialidad
)

fun PacienteResponseDto.toDomain(): Paciente = Paciente(
    usuarioId = id,
    firebaseUid = firebaseUid,
    nombreUsuario = nombreUsuario,
    fotoPerfilUrl = fotoPerfilUrl,
    psicologoId = psicologoId,
    idPaciente = idPaciente
)

fun UsuarioResponseDto.toDomain(): Usuario = when (this) {
    is PsicologoResponseDto -> UsuarioPsicologo(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombreUsuario = nombreUsuario,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        numeroColegiado = numeroColegiado,
        especialidad = especialidad
    )
    is PacienteResponseDto -> UsuarioPaciente(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombreUsuario = nombreUsuario,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        psicologoId = psicologoId,
        idPaciente = idPaciente
    )
    else -> UsuarioSinRol(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombreUsuario = nombreUsuario,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario()
    )
}

fun UsuarioPerfilResponseDto.toDomain(): UsuarioPerfil = when (this) {
    is PsicologoPerfilResponseDto -> PsicologoPerfil(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombreUsuario = nombreUsuario,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        numeroColegiado = numeroColegiado,
        especialidad = especialidad
    )
    is PacientePerfilResponseDto -> PacientePerfil(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombreUsuario = nombreUsuario,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario(),
        psicologoId = psicologoId
    )
    else -> UsuarioPerfilBasico(
        usuarioId = id,
        firebaseUid = firebaseUid,
        nombreUsuario = nombreUsuario,
        email = email,
        fotoPerfilUrl = fotoPerfilUrl,
        rol = rol.aRolUsuario()
    )
}

fun UsuarioRequest.toDto(): UsuarioRequestDto = when (this) {
    is PsicologoRequest -> PsicologoRequestDto(
        nombreUsuario = nombreUsuario,
        fotoPerfilUrl = fotoPerfilUrl,
        numeroColegiado = numeroColegiado,
        especialidad = especialidad
    )
    is PacienteRequest -> PacienteRequestDto(
        nombreUsuario = nombreUsuario,
        fotoPerfilUrl = fotoPerfilUrl,
        psicologoId = psicologoId
    )
}
