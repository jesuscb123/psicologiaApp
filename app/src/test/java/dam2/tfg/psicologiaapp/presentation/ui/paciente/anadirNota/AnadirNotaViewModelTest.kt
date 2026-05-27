package dam2.tfg.psicologiaapp.presentation.ui.paciente.anadirNota

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.usecase.CrearNotaUseCase
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnadirNotaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `alCambiarAsunto actualiza el estado correctamente`() {
        val viewModel = crearViewModel()
        val nuevoAsunto = "Asunto de prueba"

        viewModel.alCambiarAsunto(nuevoAsunto)

        assertEquals(nuevoAsunto, viewModel.uiState.value.asunto)
    }

    @Test
    fun `alCambiarAsunto no actualiza si supera el limite`() {
        val viewModel = crearViewModel()
        val asuntoLargo = "a".repeat(101)

        viewModel.alCambiarAsunto(asuntoLargo)

        assertEquals("", viewModel.uiState.value.asunto)
    }

    @Test
    fun `alCambiarDescripcion actualiza el estado correctamente`() {
        val viewModel = crearViewModel()
        val nuevaDesc = "Descripcion de prueba"

        viewModel.alCambiarDescripcion(nuevaDesc)

        assertEquals(nuevaDesc, viewModel.uiState.value.descripcion)
    }

    @Test
    fun `guardarNota muestra error si los campos estan en blanco`() = runTest {
        val viewModel = crearViewModel()

        viewModel.guardarNota()

        assertEquals("Rellena asunto y descripción", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun `guardarNota navega a NotaGuardada cuando es exito`() = runTest {
        val notaEsperada = Nota(1L, "Asunto", "Desc", "2023-10-10", 1L, 1L)
        val viewModel = crearViewModel(
            resultadoCrearNota = { _, _ -> Result.success(notaEsperada) }
        )

        viewModel.alCambiarAsunto("Asunto")
        viewModel.alCambiarDescripcion("Desc")
        viewModel.guardarNota()

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals(EventoNavegacionAnadirNota.NotaGuardada, estado.eventoNavegacion)
        assertNull(estado.mensajeError)
    }

    @Test
    fun alConsumirEventoNavegacion_limpiaEvento() = runTest {
        val viewModel = crearViewModel(
            resultadoCrearNota = { _, _ -> Result.success(Nota(1L, "A", "D", "2026-01-01", 1L, 1L)) },
        )
        viewModel.alCambiarAsunto("A")
        viewModel.alCambiarDescripcion("D")
        viewModel.guardarNota()
        advanceUntilIdle()

        viewModel.alConsumirEventoNavegacion()

        assertNull(viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun `guardarNota muestra mensaje de error cuando falla`() = runTest {
        val mensajeError = "Error de servidor"
        val viewModel = crearViewModel(
            resultadoCrearNota = { _, _ -> Result.failure(Exception(mensajeError)) }
        )

        viewModel.alCambiarAsunto("Asunto")
        viewModel.alCambiarDescripcion("Desc")
        viewModel.guardarNota()

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals(mensajeError, estado.mensajeError)
        assertNull(estado.eventoNavegacion)
    }

    private fun crearViewModel(
        resultadoCrearNota: suspend (String, String) -> Result<Nota> = { _, _ -> Result.success(Nota(0, "", "", "", 0, 0)) },
    ): AnadirNotaViewModel {
        val repository = object : FakeNotaRepository() {
            override suspend fun crearNota(asunto: String, descripcion: String) = resultadoCrearNota(asunto, descripcion)
        }
        return AnadirNotaViewModel(CrearNotaUseCase(repository))
    }
}
