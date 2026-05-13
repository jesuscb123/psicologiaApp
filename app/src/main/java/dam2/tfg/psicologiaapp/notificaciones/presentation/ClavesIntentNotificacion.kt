package dam2.tfg.psicologiaapp.notificaciones.presentation

/**
 * Claves compartidas entre el envío de la notificación, [PresentadorNotificaciones] y
 * [dam2.tfg.psicologiaapp.MainActivity]. Mantenerlas en un único punto evita errores al
 * leer extras incoherentes con los que se escriben.
 *
 * Tipos posibles:
 *  - [TIPO_CHAT]: extras `chatId`, `pacienteId`, `psicologoId`.
 *  - [TIPO_TAREA]: extras `tareaId`.
 *  - [TIPO_RIESGO]: extras `pacienteId`, `nombrePaciente` (opcional).
 */
object ClavesIntentNotificacion {

    const val EXTRA_TIPO = "notif_tipo"

    const val EXTRA_CHAT_ID = "notif_chat_id"
    const val EXTRA_PACIENTE_ID = "notif_paciente_id"
    const val EXTRA_PSICOLOGO_ID = "notif_psicologo_id"

    const val EXTRA_TAREA_ID = "notif_tarea_id"

    const val EXTRA_NOMBRE_PACIENTE = "notif_nombre_paciente"

    const val TIPO_CHAT = "CHAT"
    const val TIPO_TAREA = "TAREA"
    const val TIPO_RIESGO = "RIESGO"
}
