package dam2.tfg.psicologiaapp.presentation.ui.psicologo.fichaPaciente

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

enum class PestanaFichaPacientePsi {
    NOTAS,
    TAREAS,
}

data class FichaPacientePsicologoUiState(
    val pestanaActual: PestanaFichaPacientePsi = PestanaFichaPacientePsi.NOTAS,
    val nombreUsuarioPaciente: String = "",
    val fotoPerfilUrlPaciente: String? = null,
    val notas: List<Nota> = emptyList(),
    val tareas: List<Tarea> = emptyList(),
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val resumenIa: String? = null,
    val cargandoResumenIa: Boolean = false,
    val errorResumenIa: String? = null,
    val numeroNotasAnalizadasIa: Int = 0,
)
