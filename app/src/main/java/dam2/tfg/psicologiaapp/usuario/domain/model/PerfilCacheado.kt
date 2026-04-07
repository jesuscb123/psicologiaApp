package dam2.tfg.psicologiaapp.usuario.domain.model

data class PerfilCacheado(
    val usuarioId: Long,
    val firebaseUid: String,
    val nombre: String,
    val apellidos: String,
    val fotoPerfilUrl: String?,
    val rol: RolUsuario,
)

