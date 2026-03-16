package dam2.tfg.psicologiaapp.paciente.domain.model

data class Paciente(
    val usuarioId: Long,
    val firebaseUid: String,
    val nombreUsuario: String,
    val fotoPerfilUrl: String?,
    val psicologoId: Long?,
    val idPaciente: Long
)

