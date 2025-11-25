package dam2.tfg.psicologiaapp.usuario.data.dto

data class UsuarioDto(
    val id: Long?,
    val firebaseUid: String,
    val email: String,
    val nombreUsuario: String
)
