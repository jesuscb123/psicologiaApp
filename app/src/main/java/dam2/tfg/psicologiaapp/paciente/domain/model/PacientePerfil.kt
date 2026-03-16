package dam2.tfg.psicologiaapp.paciente.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil

data class PacientePerfil(
    override val usuarioId: Long,
    override val firebaseUid: String,
    override val nombreUsuario: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: RolUsuario = RolUsuario.PACIENTE,
    val psicologoId: Long?
) : UsuarioPerfil

