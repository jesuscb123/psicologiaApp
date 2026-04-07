package dam2.tfg.psicologiaapp.usuario.domain.model

interface UsuarioPerfil {
    val usuarioId: Long
    val firebaseUid: String
    val nombre: String
    val apellidos: String
    val email: String
    val fotoPerfilUrl: String?
    val rol: RolUsuario
}

data class UsuarioPerfilBasico(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.SIN_ROL
) : UsuarioPerfil

fun UsuarioPerfil.nombreCompleto(): String =
    listOf(nombre, apellidos).filter { it.isNotBlank() }.joinToString(" ")
