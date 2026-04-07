package dam2.tfg.psicologiaapp.paciente.data.remote

import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto

data class PacienteRequestDto(
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String? = null,
    val psicologoId: Long?,
    override val rol: String = "PACIENTE"
) : UsuarioRequestDto

data class PacienteResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val psicologoId: Long?,
    val idPaciente: Long
) : UsuarioResponseDto

data class PacientePerfilResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val psicologoId: Long?
) : UsuarioPerfilResponseDto

data class AsignarPsicologoRequestDto(
    val psicologoId: Long
)

