package dam2.tfg.psicologiaapp.presentation.components

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

/** Texto del estado de la tarea para paciente y psicólogo. */
fun textoEstadoTarea(tarea: Tarea): String = when {
    tarea.realizada -> "Completada"
    tarea.aceptadaPorPaciente -> "Aceptada"
    else -> "Pendiente de aceptación"
}
