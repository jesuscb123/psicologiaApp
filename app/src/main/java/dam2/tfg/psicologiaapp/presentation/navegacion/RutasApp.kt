package dam2.tfg.psicologiaapp.presentation.navegacion

object RutasApp {
    const val INICIAR_SESION = "iniciar_sesion"

    const val REGISTRO_SELECCION_ROL = "registro/seleccion_rol"
    const val REGISTRO_PACIENTE = "registro/paciente"
    const val REGISTRO_PSICOLOGO = "registro/psicologo"

    const val HOME_PACIENTE = "home/paciente"

    const val PERFIL_PSICOLOGO = "psicologo/{psicologoId}"
    const val ARG_PSICOLOGO_ID = "psicologoId"
    fun crearRutaPerfilPsicologo(psicologoId: String): String = "psicologo/$psicologoId"

    const val ANADIR_NOTA = "nota/anadir"

    const val PLACEHOLDER_PSICOLOGO = "psicologo/placeholder"
}

