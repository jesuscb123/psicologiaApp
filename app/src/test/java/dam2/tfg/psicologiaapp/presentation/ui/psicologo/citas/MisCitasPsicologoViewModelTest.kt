package dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas

import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MisCitasPsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun recargar_exito_limpiaError() = runTest {
        val viewModel = crearViewModel(sincronizar = { Result.success(Unit) })

        viewModel.recargar()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(null, viewModel.uiState.value.mensajeError)
    }

    @Test
    fun recargar_fallo_muestraError() = runTest {
        val viewModel = crearViewModel(sincronizar = { Result.failure(Exception("Error citas")) })

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals("Error citas", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun cambiarFiltro_actualizaEstado() {
        val viewModel = crearViewModel()

        viewModel.cambiarFiltro(FiltroMisCitas.ACTIVAS)

        assertEquals(FiltroMisCitas.ACTIVAS, viewModel.uiState.value.filtroSeleccionado)
    }

    private fun crearViewModel(
        sincronizar: suspend () -> Result<Unit> = { Result.success(Unit) },
    ): MisCitasPsicologoViewModel {
        val repo = object : FakeCitaRepository() {
            override suspend fun sincronizarMisCitasPsicologo() = sincronizar()
        }
        return MisCitasPsicologoViewModel(
            ObservarMisCitasPsicologoUseCase(repo),
            SincronizarMisCitasPsicologoUseCase(repo),
        )
    }
}
