package dam2.tfg.psicologiaapp.presentation.ui.registro

data class RegistroPacienteUiState(
    val correo: String = "",
    val errorLongitudCorreo: String? = null,
    val contrasena: String = "",
    val nombre: String = "",
    val errorLongitudNombre: String? = null,
    val apellidos: String = "",
    val errorLongitudApellidos: String? = null,
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val registroCompletado: Boolean = false
)

