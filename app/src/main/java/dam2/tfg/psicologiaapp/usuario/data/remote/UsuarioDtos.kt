package dam2.tfg.psicologiaapp.usuario.data.remote

/**
 * DTOs remotos relacionados con usuarios y perfiles,
 * alineados con los contratos del backend.
 */

sealed interface UsuarioRequestDto {
    val nombreUsuario: String
    val fotoPerfilUrl: String?
    val rol: String
}

data class PsicologoRequestDto(
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String? = null,
    val numeroColegiado: String,
    val especialidad: String,
    override val rol: String = "PSICOLOGO"
) : UsuarioRequestDto

data class PacienteRequestDto(
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String? = null,
    val psicologoId: Long?,
    override val rol: String = "PACIENTE"
) : UsuarioRequestDto

sealed interface UsuarioResponseDto {
    val id: Long
    val firebaseUid: String
    val nombreUsuario: String
    val fotoPerfilUrl: String?
    val rol: String
}

data class PsicologoResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val numeroColegiado: String,
    val especialidad: String
) : UsuarioResponseDto

data class PacienteResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val psicologoId: Long?,
    val idPaciente: Long
) : UsuarioResponseDto

data class UsuarioBasicoResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: String
) : UsuarioResponseDto

sealed interface UsuarioPerfilResponseDto {
    val id: Long
    val firebaseUid: String
    val nombreUsuario: String
    val email: String
    val fotoPerfilUrl: String?
    val rol: String
}

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

data class PacientePerfilResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val psicologoId: Long?
) : UsuarioPerfilResponseDto

data class UsuarioPerfilBasicoResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: String
) : UsuarioPerfilResponseDto

data class AsignarPsicologoRequestDto(
    val psicologoId: Long
)

data class ActualizarEmailRequestDto(
    val nuevoEmail: String
)
