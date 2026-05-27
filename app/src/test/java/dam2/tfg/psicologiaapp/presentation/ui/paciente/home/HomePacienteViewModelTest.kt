package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.ObservarNoLeidosEnChatUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.BorrarNotaUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.usecase.AceptarTareaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.MarcarTareaRealizadaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomePacienteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val perfilPaciente = PacientePerfil(
        usuarioId = 1L,
        firebaseUid = "uid-paciente",
        nombre = "Ana",
        apellidos = "López",
        email = "ana@test.com",
        fotoPerfilUrl = null,
        psicologoId = 2L,
    )

    @Test
    fun sincronizarSiProcede_sinPerfil_muestraError() = runTest {
        val viewModel = crearViewModel(
            getPerfil = { Result.failure(Exception("Sin perfil")) },
        )

        viewModel.sincronizarSiProcede()
        advanceUntilIdle()

        assertEquals("No se pudo cargar el perfil", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun sincronizarSiProcede_conPerfil_cargaDatos() = runTest {
        val perfilFlow = MutableStateFlow<PerfilCacheado?>(
            PerfilCacheado(1L, "uid-paciente", "Ana", "López", null, RolUsuario.PACIENTE, 2L),
        )
        val viewModel = crearViewModel(
            perfilFlow = perfilFlow,
            getPerfil = { Result.success(perfilPaciente) },
        )
        advanceUntilIdle()

        viewModel.sincronizarSiProcede()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.cargando)
        assertEquals(perfilPaciente.nombre, viewModel.uiState.value.perfilPaciente?.nombre)
    }

    @Test
    fun aceptarTarea_fallo_muestraError() = runTest {
        val viewModel = crearViewModel(
            aceptarTarea = { Result.failure(Exception("No se pudo aceptar")) },
        )

        viewModel.aceptarTarea(1L)
        advanceUntilIdle()

        assertEquals("No se pudo aceptar", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun eliminarNota_exito_limpiaError() = runTest {
        val viewModel = crearViewModel()

        viewModel.eliminarNota(1L)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.mensajeError)
    }

    @Test
    fun uiState_emitePerfilDesdeCache() = runTest {
        val perfilFlow = MutableStateFlow<PerfilCacheado?>(
            PerfilCacheado(1L, "uid-paciente", "Ana", "López", null, RolUsuario.PACIENTE, 2L),
        )
        val viewModel = crearViewModel(perfilFlow = perfilFlow)

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val estado = awaitItem()
            assertEquals("Ana", estado.perfilPaciente?.nombre)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun crearViewModel(
        perfilFlow: MutableStateFlow<PerfilCacheado?> = MutableStateFlow(null),
        getPerfil: suspend () -> Result<UsuarioPerfil> = { Result.success(perfilPaciente) },
        aceptarTarea: suspend (Long) -> Result<Tarea> = { Result.failure(NotImplementedError()) },
    ): HomePacienteViewModel {
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual() = getPerfil()
        }
        val cacheRepo = object : FakeUsuarioCacheRepository() {
            override fun observarPerfilCacheado() = perfilFlow
            override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {
                perfilFlow.value = PerfilCacheado(
                    perfil.usuarioId,
                    perfil.firebaseUid,
                    perfil.nombre,
                    perfil.apellidos,
                    perfil.fotoPerfilUrl,
                    perfil.rol,
                    (perfil as? PacientePerfil)?.psicologoId,
                )
            }
        }
        val tareaRepo = object : FakeTareaRepository() {
            override suspend fun aceptarTarea(tareaId: Long) = aceptarTarea(tareaId)
        }
        return HomePacienteViewModel(
            observarPerfilCacheadoUseCase = ObservarPerfilCacheadoUseCase(cacheRepo),
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(usuarioRepo, cacheRepo),
            getPerfilActualUseCase = GetPerfilActualUseCase(usuarioRepo),
            observarPsicologosUseCase = ObservarPsicologosUseCase(FakePsicologoRepository()),
            sincronizarPsicologosUseCase = SincronizarPsicologosUseCase(FakePsicologoRepository()),
            observarNotasPacienteActualUseCase = ObservarNotasPacienteActualUseCase(FakeNotaRepository()),
            observarTareasPacienteActualUseCase = ObservarTareasPacienteActualUseCase(FakeTareaRepository()),
            sincronizarNotasPacienteActualUseCase = SincronizarNotasPacienteActualUseCase(FakeNotaRepository()),
            sincronizarTareasPacienteActualUseCase = SincronizarTareasPacienteActualUseCase(FakeTareaRepository()),
            observarMisCitasPacienteUseCase = ObservarMisCitasPacienteUseCase(FakeCitaRepository()),
            sincronizarMisCitasPacienteUseCase = SincronizarMisCitasPacienteUseCase(FakeCitaRepository()),
            aceptarTareaUseCase = AceptarTareaUseCase(tareaRepo),
            marcarTareaRealizadaUseCase = MarcarTareaRealizadaUseCase(tareaRepo),
            borrarNotaUseCase = BorrarNotaUseCase(FakeNotaRepository()),
            observarNoLeidosEnChatUseCase = ObservarNoLeidosEnChatUseCase(FakeChatRepository()),
        )
    }
}
