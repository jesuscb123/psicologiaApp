package dam2.tfg.psicologiaapp.presentation.ui.psicologo.home

import app.cash.turbine.test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dam2.tfg.psicologiaapp.chat.domain.usecase.ObservarNoLeidosEnChatUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.ObservarAlertasRiesgoUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeAlertaRiesgoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomePsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val paciente = Paciente(
        usuarioId = 3L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = null,
        psicologoId = 2L,
        idPaciente = 3L,
    )

    @Test
    fun sincronizarSiProcede_exito_dejaDeCargar() = runTest {
        val pacientesFlow = MutableStateFlow(listOf(paciente))
        val viewModel = crearViewModel(
            pacientesFlow = pacientesFlow,
            getPacientes = { Result.success(listOf(paciente)) },
        )
        advanceUntilIdle()

        viewModel.sincronizarSiProcede()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.listaPacientes.size)
    }

    @Test
    fun sincronizarSiProcede_falloPacientes_muestraError() = runTest {
        val viewModel = crearViewModel(
            getPacientes = { Result.failure(Exception("Error pacientes")) },
        )
        advanceUntilIdle()

        viewModel.sincronizarSiProcede()
        advanceUntilIdle()

        assertEquals("Error pacientes", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun uiState_emiteListaPacientes() = runTest {
        val pacientesFlow = MutableStateFlow(emptyList<Paciente>())
        val viewModel = crearViewModel(pacientesFlow = pacientesFlow)

        viewModel.uiState.test {
            skipItems(1)
            pacientesFlow.value = listOf(paciente)
            advanceUntilIdle()
            val estado = awaitItem()
            assertEquals(1, estado.listaPacientes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun crearViewModel(
        pacientesFlow: MutableStateFlow<List<Paciente>> = MutableStateFlow(emptyList()),
        getPacientes: suspend () -> Result<List<Paciente>> = { Result.success(emptyList()) },
    ): HomePsicologoViewModel {
        val psicologoRepo = object : FakePsicologoRepository() {
            override fun observarPacientesDePsicologo() = pacientesFlow
            override suspend fun getPacientesDePsicologo() = getPacientes()
        }
        val user = mock<FirebaseUser>()
        whenever(user.uid).thenReturn("uid-psicologo")
        val firebaseAuth = mock<FirebaseAuth>()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        val usuarioRepo = FakeUsuarioRepository()
        val cacheRepo = FakeUsuarioCacheRepository()
        return HomePsicologoViewModel(
            firebaseAuth = firebaseAuth,
            observarPacientesDePsicologoUseCase = ObservarPacientesDePsicologoUseCase(psicologoRepo),
            sincronizarPacientesDePsicologoUseCase = SincronizarPacientesDePsicologoUseCase(psicologoRepo),
            observarMisCitasPsicologoUseCase = ObservarMisCitasPsicologoUseCase(FakeCitaRepository()),
            sincronizarMisCitasPsicologoUseCase = SincronizarMisCitasPsicologoUseCase(FakeCitaRepository()),
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(usuarioRepo, cacheRepo),
            observarNoLeidosEnChatUseCase = ObservarNoLeidosEnChatUseCase(FakeChatRepository()),
            observarAlertasRiesgoUseCase = ObservarAlertasRiesgoUseCase(FakeAlertaRiesgoRepository()),
        )
    }
}
