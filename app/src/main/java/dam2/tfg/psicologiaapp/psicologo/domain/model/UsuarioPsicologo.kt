package dam2.tfg.psicologiaapp.psicologo.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario

data class UsuarioPsicologo(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PSICOLOGO,
    val numeroColegiado: String,
    val especialidad: String
) : Usuario

