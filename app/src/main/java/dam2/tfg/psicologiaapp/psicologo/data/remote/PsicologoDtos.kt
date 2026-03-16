package dam2.tfg.psicologiaapp.psicologo.data.remote

import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto

data class PsicologoRequestDto(
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String? = null,
    val numeroColegiado: String,
    val especialidad: String,
    override val rol: String = "PSICOLOGO"
) : UsuarioRequestDto

data class PsicologoResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val numeroColegiado: String,
    val especialidad: String
) : UsuarioResponseDto

data class PsicologoPerfilResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val numeroColegiado: String,
    val especialidad: String
) : UsuarioPerfilResponseDto

