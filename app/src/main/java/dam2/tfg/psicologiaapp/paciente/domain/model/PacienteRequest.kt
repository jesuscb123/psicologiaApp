package dam2.tfg.psicologiaapp.paciente.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest

data class PacienteRequest(
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String? = null,
    val psicologoId: Long?
) : UsuarioRequest

