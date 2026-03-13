package dam2.tfg.psicologiaapp.usuario.domain.model

/**
 * Modelos de dominio para usuarios y perfiles,
 * alineados con el backend pero desacoplados de la capa de red.
 */

enum class RolUsuario {
    PSICOLOGO,
    PACIENTE,
    SIN_ROL
}

sealed interface Usuario {
    val usuarioId: Long
    val firebaseUid: String
    val nombreUsuario: String
    val fotoPerfilUrl: String?
    val rol: RolUsuario
}

data class UsuarioPsicologo(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PSICOLOGO,
    val numeroColegiado: String,
    val especialidad: String
) : Usuario

data class UsuarioPaciente(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PACIENTE,
    val psicologoId: Long?,
    val idPaciente: Long
) : Usuario

data class UsuarioSinRol(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.SIN_ROL
) : Usuario

sealed interface UsuarioPerfil {
    val usuarioId: Long
    val firebaseUid: String
    val nombreUsuario: String
    val email: String
    val fotoPerfilUrl: String?
    val rol: RolUsuario
}

data class PsicologoPerfil(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PSICOLOGO,
    val numeroColegiado: String,
    val especialidad: String
) : UsuarioPerfil

data class PacientePerfil(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PACIENTE,
    val psicologoId: Long?
) : UsuarioPerfil

data class UsuarioPerfilBasico(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.SIN_ROL
) : UsuarioPerfil

data class Psicologo(
    val usuarioId: Long,
    val firebaseUid: String,
    val nombreUsuario: String,
    val fotoPerfilUrl: String?,
    val numeroColegiado: String,
    val especialidad: String
)

data class Paciente(
    val usuarioId: Long,
    val firebaseUid: String,
    val nombreUsuario: String,
    val fotoPerfilUrl: String?,
    val psicologoId: Long?,
    val idPaciente: Long
)

/**
 * Modelo de dominio para crear usuario (alineado con UsuarioRequestDto del backend).
 */
sealed interface UsuarioRequest {
    val nombreUsuario: String
    val fotoPerfilUrl: String?
}

data class PsicologoRequest(
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String? = null,
    val numeroColegiado: String,
    val especialidad: String
) : UsuarioRequest

data class PacienteRequest(
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String? = null,
    val psicologoId: Long?
) : UsuarioRequest
