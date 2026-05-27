package dam2.tfg.psicologiaapp.presentation.ui.paciente.ajustes

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.EstablecerModoTemaUseCase
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeTemaPreferenciasRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AjustesPacienteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_observaModoTema() = runTest {
        val repo = FakeTemaPreferenciasRepository(ModoTemaApp.Claro)
        val viewModel = crearViewModel(repo)

        advanceUntilIdle()

        assertEquals(ModoTemaApp.Claro, viewModel.uiState.value.modoTema)
    }

    @Test
    fun fijarModoTema_actualizaPreferencia() = runTest {
        var modoGuardado: ModoTemaApp? = null
        val repo = object : FakeTemaPreferenciasRepository(ModoTemaApp.SeguirSistema) {
            override suspend fun establecerModoTema(modo: ModoTemaApp) {
                modoGuardado = modo
            }
        }
        val viewModel = crearViewModel(repo)

        viewModel.fijarModoTema(ModoTemaApp.Oscuro)
        advanceUntilIdle()

        assertEquals(ModoTemaApp.Oscuro, modoGuardado)
    }

    private fun crearViewModel(repo: FakeTemaPreferenciasRepository): AjustesPacienteViewModel =
        AjustesPacienteViewModel(
            ObservarModoTemaUseCase(repo),
            EstablecerModoTemaUseCase(repo),
        )
}
