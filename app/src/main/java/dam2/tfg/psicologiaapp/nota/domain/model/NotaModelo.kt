package dam2.tfg.psicologiaapp.nota.domain.model

/**
 * Modelo de dominio de nota, alineado con el backend.
 */

data class Nota(
    val id: Long,
    val asunto: String,
    val descripcion: String,
    val pacienteId: Long,
    val psicologoId: Long,
)
