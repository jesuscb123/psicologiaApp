package dam2.tfg.psicologiaapp.presentation.ui.registro

data class RegistroPsicologoUiState(
    val correo: String = "",
    val contrasena: String = "",
    val nombreUsuario: String = "",
    val numeroColegiado: String = "",
    val especialidad: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val registroCompletado: Boolean = false
)

