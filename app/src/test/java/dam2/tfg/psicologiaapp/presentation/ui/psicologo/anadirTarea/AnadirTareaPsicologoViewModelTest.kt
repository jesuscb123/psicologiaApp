package dam2.tfg.psicologiaapp.presentation.ui.psicologo.anadirTarea

import androidx.lifecycle.SavedStateHandle
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.usecase.CrearTareaUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnadirTareaPsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun alCambiarTitulo_actualizaEstado() {
        val viewModel = crearViewModel(pacienteId = 5L)

        viewModel.alCambiarTitulo("Tarea 1")

        assertEquals("Tarea 1", viewModel.uiState.value.titulo)
    }

    @Test
    fun guardarTarea_sinPacienteId_muestraError() {
        val viewModel = crearViewModel(pacienteId = 0L)

        viewModel.alCambiarTitulo("Título")
        viewModel.alCambiarDescripcion("Descripción")
        viewModel.guardarTarea()

        assertEquals("Identificador de paciente no válido", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun guardarTarea_camposVacios_muestraError() {
        val viewModel = crearViewModel(pacienteId = 5L)

        viewModel.guardarTarea()

        assertEquals("Rellena título y descripción", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun guardarTarea_exito_navega() = runTest {
        val tarea = Tarea(1L, "Título", "Desc", "2026-01-01", false, false, 2L, 5L)
        val viewModel = crearViewModel(
            pacienteId = 5L,
            crearTarea = { _, titulo, desc ->
                Result.success(tarea.copy(titulo = titulo, descripcion = desc))
            },
        )
        viewModel.alCambiarTitulo("Título")
        viewModel.alCambiarDescripcion("Descripción")

        viewModel.guardarTarea()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals(EventoNavegacionAnadirTareaPsicologo.TareaGuardada, estado.eventoNavegacion)
    }

    @Test
    fun alConsumirEventoNavegacion_limpiaEvento() {
        val viewModel = crearViewModel(pacienteId = 5L)

        viewModel.alConsumirEventoNavegacion()

        assertNull(viewModel.uiState.value.eventoNavegacion)
    }

    private fun crearViewModel(
        pacienteId: Long,
        crearTarea: suspend (Long, String, String) -> Result<Tarea> = { _, _, _ ->
            Result.failure(NotImplementedError())
        },
    ): AnadirTareaPsicologoViewModel {
        val repo = object : FakeTareaRepository() {
            override suspend fun crearTarea(pacienteId: Long, titulo: String, descripcion: String) =
                crearTarea(pacienteId, titulo, descripcion)
        }
        val savedState = SavedStateHandle(mapOf(RutasApp.ARG_PACIENTE_ID to pacienteId))
        return AnadirTareaPsicologoViewModel(savedState, CrearTareaUseCase(repo))
    }
}
