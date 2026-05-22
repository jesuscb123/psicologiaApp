package dam2.tfg.psicologiaapp.presentation.navegacion

object RutasApp {
    const val SPLASH = "splash"
    const val INICIAR_SESION = "iniciar_sesion"

    const val REGISTRO_SELECCION_ROL = "registro/seleccion_rol"
    const val REGISTRO_PACIENTE = "registro/paciente"
    const val REGISTRO_PSICOLOGO = "registro/psicologo"

    /** Ruta raíz del flujo paciente (NavHost anidado + menú lateral). */
    const val GRAFO_PACIENTE = "grafo/paciente"

    const val ARG_PSICOLOGO_ID = "psicologoId"

    /** Argumento de ruta: id del paciente en el flujo psicólogo (Long). */
    const val ARG_PACIENTE_ID = "pacienteId"

    /** Ruta raíz del flujo psicólogo (NavHost anidado + menú lateral). */
    const val GRAFO_PSICOLOGO = "grafo/psicologo"
}

/** Rutas del [androidx.navigation.compose.NavHost] interno del grafo paciente. */
object RutasGrafoPaciente {
    const val HOME = "home"
    const val NOTAS = "notas"
    const val TAREAS = "tareas"
    const val ANADIR_NOTA = "nota/anadir"
    const val CITAS_MENU = "citas"
    const val AGENDAR_CITA = "citas/agendar"
    const val MIS_CITAS = "citas/mis_citas"
    const val PERFIL_PSICOLOGO = "psicologo/{psicologoId}"
    const val AJUSTES = "ajustes"
    const val ACERCA = "acerca"
    const val CHAT_PSICOLOGO = "chat/psicologo"

    fun crearRutaPerfilPsicologo(psicologoId: String): String = "psicologo/$psicologoId"
}

/** Rutas del [androidx.navigation.compose.NavHost] interno del grafo psicólogo. */
object RutasGrafoPsicologo {
    const val HOME = "home"
    const val FICHA_PACIENTE = "ficha_paciente/{pacienteId}"
    const val ANADIR_TAREA = "tarea/anadir/{pacienteId}"
    const val MIS_CITAS = "citas/mis_citas"
    const val AJUSTES = "ajustes"
    const val AJUSTES_HUB = "ajustes/hub"
    const val ACERCA = "acerca"
    const val CHAT_PACIENTE = "chat/paciente/{pacienteId}"

    fun crearRutaFichaPaciente(pacienteId: Long): String = "ficha_paciente/$pacienteId"

    fun crearRutaAnadirTarea(pacienteId: Long): String = "tarea/anadir/$pacienteId"

    fun crearRutaChatPaciente(pacienteId: Long): String = "chat/paciente/$pacienteId"
}

