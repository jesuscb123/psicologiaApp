package dam2.tfg.psicologiaapp.tarea.domain.model

import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import dam2.tfg.psicologiaapp.usuario.domain.model.Psicologo

/**
 * Modelo de dominio de tarea, alineado con el backend.
 */

data class Tarea(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val horaEnvio: String,
    val realizada: Boolean,
    val psicologo: Psicologo,
    val paciente: Paciente
)
