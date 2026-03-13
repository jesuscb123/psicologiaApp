package dam2.tfg.psicologiaapp.nota.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import dam2.tfg.psicologiaapp.usuario.domain.model.Psicologo

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
