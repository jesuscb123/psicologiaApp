package dam2.tfg.psicologiaapp.nota.domain.model

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

/**
 * Modelo de dominio de nota, alineado con el backend.
 */

data class Nota(
    val id: Long,
    val asunto: String,
    val descripcion: String,
    val paciente: Paciente,
    val psicologo: Psicologo
)
