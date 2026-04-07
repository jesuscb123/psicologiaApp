package dam2.tfg.psicologiaapp.psicologo.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil

data class PsicologoPerfil(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PSICOLOGO,
    val numeroColegiado: String,
    val especialidad: String,
    val descripcion: String? = null,
) : UsuarioPerfil

