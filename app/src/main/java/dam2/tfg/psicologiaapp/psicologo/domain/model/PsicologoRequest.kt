package dam2.tfg.psicologiaapp.psicologo.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest

data class PsicologoRequest(
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String? = null,
    val numeroColegiado: String,
    val especialidad: String,
    val descripcion: String? = null,
) : UsuarioRequest

