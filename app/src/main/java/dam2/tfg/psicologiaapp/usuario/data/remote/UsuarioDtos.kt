package dam2.tfg.psicologiaapp.usuario.data.remote

/**
 * DTOs remotos relacionados con usuarios y perfiles,
 * alineados con los contratos del backend.
 */

interface UsuarioRequestDto {
    val nombreUsuario: String
    val fotoPerfilUrl: String?
    val rol: String
}

interface UsuarioResponseDto {
    val id: Long
    val firebaseUid: String
    val nombreUsuario: String
    val fotoPerfilUrl: String?
    val rol: String
}

data class UsuarioBasicoResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: String
) : UsuarioResponseDto

interface UsuarioPerfilResponseDto {
    val id: Long
    val firebaseUid: String
    val nombreUsuario: String
    val email: String
    val fotoPerfilUrl: String?
    val rol: String
}

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
