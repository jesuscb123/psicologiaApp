package dam2.tfg.psicologiaapp.psicologo.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest

data class PsicologoRequest(
    override val nombreUsuario: String,
    override val fotoPerfilUrl: String? = null,
    val numeroColegiado: String,
    val especialidad: String
) : UsuarioRequest

