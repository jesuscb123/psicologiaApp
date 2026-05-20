package dam2.tfg.psicologiaapp.presentation.ui.registro.psicologo

data class RegistroPsicologoUiState(
    val correo: String = "",
    val errorLongitudCorreo: String? = null,
    val contrasena: String = "",
    val nombre: String = "",
    val errorLongitudNombre: String? = null,
    val apellidos: String = "",
    val errorLongitudApellidos: String? = null,
    val numeroColegiado: String = "",
    val errorLongitudNumeroColegiado: String? = null,
    val especialidades: List<String> = emptyList(),
    val especialidadInput: String = "",
    val errorEspecialidadInput: String? = null,
    val descripcion: String = "",
    val errorLongitudDescripcion: String? = null,
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val registroCompletado: Boolean = false
)
