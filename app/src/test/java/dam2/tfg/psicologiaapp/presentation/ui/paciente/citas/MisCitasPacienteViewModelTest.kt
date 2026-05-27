package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaPersistido
import dam2.tfg.psicologiaapp.cita.domain.usecase.CancelarCitaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MisCitasPacienteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val citaEjemplo = Cita(
        id = 1L,
        inicio = "2026-12-01T10:00:00+01:00",
        fin = "2026-12-01T11:00:00+01:00",
        psicologoId = 2L,
        pacienteId = 3L,
        nombrePsicologo = "Dr House",
        nombrePaciente = "Ana",
        estadoPersistido = EstadoCitaPersistido.RESERVADA,
        estadoCalculado = EstadoCitaCalculado.ACTIVA,
    )

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
        val viewModel = crearViewModel(sincronizar = { Result.failure(Exception("Sin conexión")) })

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals("Sin conexión", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun cancelarCita_exito_dejaDeCargar() = runTest {
        val viewModel = crearViewModel(
            cancelar = { Result.success(citaEjemplo) },
        )

        viewModel.cancelarCita(1L)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cambiarFiltro_actualizaEstado() {
        val viewModel = crearViewModel()

        viewModel.cambiarFiltro(FiltroMisCitas.FINALIZADAS)

        assertEquals(FiltroMisCitas.FINALIZADAS, viewModel.uiState.value.filtroSeleccionado)
    }

    @Test
    fun puedeCancelar_soloActiva() {
        val viewModel = crearViewModel()

        assertTrue(viewModel.puedeCancelar(EstadoCitaCalculado.ACTIVA))
        assertFalse(viewModel.puedeCancelar(EstadoCitaCalculado.CANCELADA))
    }

    private fun crearViewModel(
        sincronizar: suspend () -> Result<Unit> = { Result.success(Unit) },
        cancelar: suspend (Long) -> Result<Cita> = { Result.failure(NotImplementedError()) },
        citasFlow: MutableStateFlow<List<Cita>> = MutableStateFlow(emptyList()),
    ): MisCitasPacienteViewModel {
        val repo = object : FakeCitaRepository() {
            override suspend fun sincronizarMisCitasPaciente() = sincronizar()
            override suspend fun cancelarCita(citaId: Long) = cancelar(citaId)
            override fun observarMisCitasPaciente() = citasFlow
        }
        return MisCitasPacienteViewModel(
            ObservarMisCitasPacienteUseCase(repo),
            SincronizarMisCitasPacienteUseCase(repo),
            CancelarCitaUseCase(repo),
        )
    }
}
