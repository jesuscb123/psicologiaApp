package dam2.tfg.psicologiaapp.usuario.domain.model

data class Usuario(
    val idBackend: Long?,
    val firebaseUid: String,
    val email: String,
    val nombreUsuario: String
)