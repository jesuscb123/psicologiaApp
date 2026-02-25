package dam2.tfg.psicologiaapp.usuario.domain.model

data class Usuario(
    val idBackend: Long = 0,
    val firebaseUid: String,
    val email: String,
    val nombreUsuario: String,
    val fotoPerfil: String? = null
)