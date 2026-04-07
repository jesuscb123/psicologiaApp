package dam2.tfg.psicologiaapp.paciente.domain.model

data class Paciente(
    val usuarioId: Long,
    val firebaseUid: String,
    val nombre: String,
    val apellidos: String,
    val fotoPerfilUrl: String?,
    val psicologoId: Long?,
    val idPaciente: Long
)

