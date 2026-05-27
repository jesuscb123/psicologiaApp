package dam2.tfg.psicologiaapp.presentation

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeTemaPreferenciasRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModoTemaActividadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun modoTema_emiteValorDelRepositorio() = runTest {
        val viewModel = ModoTemaActividadViewModel(
            ObservarModoTemaUseCase(FakeTemaPreferenciasRepository(ModoTemaApp.Oscuro)),
        )

        val job = backgroundScope.launch { viewModel.modoTema.collect { } }
        advanceUntilIdle()

        assertEquals(ModoTemaApp.Oscuro, viewModel.modoTema.value)
        job.cancel()
    }
}
