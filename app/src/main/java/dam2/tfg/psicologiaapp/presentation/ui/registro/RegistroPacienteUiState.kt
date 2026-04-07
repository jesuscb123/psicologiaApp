package dam2.tfg.psicologiaapp.presentation.ui.registro

data class RegistroPacienteUiState(
    val correo: String = "",
    val contrasena: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val registroCompletado: Boolean = false
)

