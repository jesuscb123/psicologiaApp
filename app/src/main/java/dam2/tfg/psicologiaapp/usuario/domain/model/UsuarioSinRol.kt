package dam2.tfg.psicologiaapp.usuario.domain.model

data class UsuarioSinRol(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.SIN_ROL
) : Usuario