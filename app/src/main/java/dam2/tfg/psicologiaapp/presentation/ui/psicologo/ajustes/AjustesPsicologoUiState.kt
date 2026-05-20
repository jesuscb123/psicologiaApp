package dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes

data class AjustesPsicologoUiState(
    val descripcion: String = "",
    val descripcionInicial: String = "",
    val especialidades: List<String> = emptyList(),
    val especialidadesIniciales: List<String> = emptyList(),
    val especialidadInput: String = "",
    val errorEspecialidadInput: String? = null,
    val cargando: Boolean = false,
    val yaSeHaCargado: Boolean = false,
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeOk: String? = null,
) {
    val hayCambios: Boolean
        get() = descripcion != descripcionInicial || especialidades != especialidadesIniciales
}
