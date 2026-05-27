package dam2.tfg.psicologiaapp.presentation.ui.psicologo.fichaPaciente

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.LimpiarAlertaRiesgoPacienteUseCase
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa
import dam2.tfg.psicologiaapp.resumenIa.domain.usecase.GenerarResumenIaPacienteUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasDePacienteUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasDePacienteUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeAlertaRiesgoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeResumenIaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FichaPacientePsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun cambiarPestana_actualizaEstado() = runTest {
        val viewModel = crearViewModel(pacienteId = 3L)

        viewModel.cambiarPestana(PestanaFichaPacientePsi.TAREAS)

        assertEquals(PestanaFichaPacientePsi.TAREAS, viewModel.uiState.value.pestanaActual)
    }

    @Test
    fun recargar_sinPacienteId_muestraError() = runTest {
        val viewModel = crearViewModel(pacienteId = 0L)

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals("Identificador de paciente no válido", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun recargar_exito_dejaDeCargar() = runTest {
        val viewModel = crearViewModel(pacienteId = 3L)
        advanceUntilIdle()

        viewModel.recargar()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun generarResumenIa_exito_guardaResumen() = runTest {
        val resumen = ResumenIa("Resumen IA", 5, "2026-01-01", "gpt")
        val viewModel = crearViewModel(
            pacienteId = 3L,
            generarResumen = { Result.success(resumen) },
        )
        advanceUntilIdle()

        viewModel.generarResumenIa()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals("Resumen IA", estado.resumenIa)
        assertEquals(5, estado.numeroNotasAnalizadasIa)
        assertFalse(estado.cargandoResumenIa)
    }

    @Test
    fun generarResumenIa_fallo_muestraError() = runTest {
        val viewModel = crearViewModel(
            pacienteId = 3L,
            generarResumen = { Result.failure(Exception("IA no disponible")) },
        )
        advanceUntilIdle()

        viewModel.generarResumenIa()
        advanceUntilIdle()

        assertEquals("IA no disponible", viewModel.uiState.value.errorResumenIa)
    }

    @Test
    fun descartarResumenIa_limpiaEstado() = runTest {
        val viewModel = crearViewModel(pacienteId = 3L)
        advanceUntilIdle()

        viewModel.descartarResumenIa()

        assertEquals(null, viewModel.uiState.value.resumenIa)
        assertEquals(0, viewModel.uiState.value.numeroNotasAnalizadasIa)
    }

    private fun crearViewModel(
        pacienteId: Long,
        generarResumen: suspend (Long) -> Result<ResumenIa> = { Result.failure(NotImplementedError()) },
    ): FichaPacientePsicologoViewModel {
        val pacienteDao = mock<PacienteDao>()
        whenever(pacienteDao.observarPorId(pacienteId)).thenReturn(emptyFlow())
        val user = mock<FirebaseUser>()
        whenever(user.uid).thenReturn("uid-psicologo")
        val firebaseAuth = mock<FirebaseAuth>()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        val resumenRepo = object : FakeResumenIaRepository() {
            override suspend fun generarResumenNotasPaciente(pacienteId: Long) = generarResumen(pacienteId)
        }
        val savedState = SavedStateHandle(mapOf(RutasApp.ARG_PACIENTE_ID to pacienteId))
        return FichaPacientePsicologoViewModel(
            savedStateHandle = savedState,
            firebaseAuth = firebaseAuth,
            sincronizarPacientesDePsicologoUseCase = SincronizarPacientesDePsicologoUseCase(FakePsicologoRepository()),
            pacienteDao = pacienteDao,
            observarNotasDePacienteUseCase = ObservarNotasDePacienteUseCase(FakeNotaRepository()),
            observarTareasDePacienteUseCase = ObservarTareasDePacienteUseCase(FakeTareaRepository()),
            sincronizarNotasDePacienteUseCase = SincronizarNotasDePacienteUseCase(FakeNotaRepository()),
            sincronizarTareasDePacienteUseCase = SincronizarTareasDePacienteUseCase(FakeTareaRepository()),
            generarResumenIaPacienteUseCase = GenerarResumenIaPacienteUseCase(resumenRepo),
            limpiarAlertaRiesgoPacienteUseCase = LimpiarAlertaRiesgoPacienteUseCase(FakeAlertaRiesgoRepository()),
        )
    }
}
