package dam2.tfg.psicologiaapp.cita.domain.model

data class Cita(
    val id: Long,
    /** ISO-8601 con offset. */
    val inicio: String,
    /** ISO-8601 con offset. */
    val fin: String,
    val psicologoId: Long,
    val pacienteId: Long,
    val nombrePsicologo: String,
    val nombrePaciente: String,
    val estadoPersistido: EstadoCitaPersistido,
    val estadoCalculado: EstadoCitaCalculado,
)

