package dam2.tfg.psicologiaapp.presentation.navegacion

object RutasApp {
    const val INICIAR_SESION = "iniciar_sesion"

    const val REGISTRO_SELECCION_ROL = "registro/seleccion_rol"
    const val REGISTRO_PACIENTE = "registro/paciente"
    const val REGISTRO_PSICOLOGO = "registro/psicologo"

    /** Ruta raíz del flujo paciente (NavHost anidado + menú lateral). */
    const val GRAFO_PACIENTE = "grafo/paciente"

    const val ARG_PSICOLOGO_ID = "psicologoId"

    const val PLACEHOLDER_PSICOLOGO = "psicologo/placeholder"
}

/** Rutas del [androidx.navigation.compose.NavHost] interno del grafo paciente. */
object RutasGrafoPaciente {
    const val HOME = "home"
    const val ANADIR_NOTA = "nota/anadir"
    const val PERFIL_PSICOLOGO = "psicologo/{psicologoId}"
    const val AJUSTES = "ajustes"
    const val ACERCA = "acerca"

    fun crearRutaPerfilPsicologo(psicologoId: String): String = "psicologo/$psicologoId"
}

