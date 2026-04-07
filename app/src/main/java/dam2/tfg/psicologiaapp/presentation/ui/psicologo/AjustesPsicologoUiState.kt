package dam2.tfg.psicologiaapp.presentation.ui.psicologo

data class AjustesPsicologoUiState(
    val descripcion: String = "",
    val descripcionInicial: String = "",
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeOk: String? = null,
) {
    val hayCambios: Boolean get() = descripcion != descripcionInicial
}

