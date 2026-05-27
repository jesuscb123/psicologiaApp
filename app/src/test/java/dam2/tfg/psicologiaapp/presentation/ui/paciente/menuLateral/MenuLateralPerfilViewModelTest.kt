package dam2.tfg.psicologiaapp.presentation.ui.paciente.menuLateral

import android.content.Context
import dam2.tfg.psicologiaapp.auth.domain.usecase.CerrarSesionUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.DarDeBajaFcmTokenUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.EstablecerModoTemaUseCase
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotificacionesRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeTemaPreferenciasRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.LimpiarTodosDatosLocalesUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarFotoPerfilUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class MenuLateralPerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val perfilPaciente = PacientePerfil(
        usuarioId = 1L,
        firebaseUid = "uid-paciente",
        nombre = "Ana",
        apellidos = "García",
        email = "ana@test.com",
        fotoPerfilUrl = null,
        psicologoId = null,
    )

    @Test
    fun observarPerfilCacheado_actualizaNombre() = runTest {
        val perfilFlow = MutableStateFlow<PerfilCacheado?>(
            PerfilCacheado(1L, "uid", "Ana", "García", null, RolUsuario.PACIENTE),
        )
        val viewModel = crearViewModel(perfilFlow = perfilFlow)
        advanceUntilIdle()

        assertEquals("Ana García", viewModel.uiState.value.nombreUsuario)
    }

    @Test
    fun fijarModoTema_guardaPreferencia() = runTest {
        var modoGuardado: ModoTemaApp? = null
        val temaRepo = object : FakeTemaPreferenciasRepository(ModoTemaApp.Claro) {
            override suspend fun establecerModoTema(modo: ModoTemaApp) {
                modoGuardado = modo
            }
        }
        val viewModel = crearViewModel(temaRepo = temaRepo)
        advanceUntilIdle()

        viewModel.fijarModoTema(ModoTemaApp.Oscuro)
        advanceUntilIdle()

        assertEquals(ModoTemaApp.Oscuro, modoGuardado)
    }

    @Test
    fun cerrarSesion_exito_emiteNavegacion() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.cerrarSesion()
        advanceUntilIdle()

        assertEquals(EventoNavegacionMenuLateral.SesionCerrada, viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun alConsumirEventoNavegacion_limpiaEvento() = runTest {
        val viewModel = crearViewModel()
        viewModel.cerrarSesion()
        advanceUntilIdle()

        viewModel.alConsumirEventoNavegacion()

        assertNull(viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun recargarPerfil_exito_actualizaNombre() = runTest {
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.success(perfilPaciente)
        }
        val viewModel = crearViewModel(usuarioRepo = usuarioRepo)

        viewModel.recargarPerfil()
        advanceUntilIdle()

        assertEquals("Ana García", viewModel.uiState.value.nombreUsuario)
    }

    private fun crearViewModel(
        perfilFlow: MutableStateFlow<PerfilCacheado?> = MutableStateFlow(null),
        usuarioRepo: FakeUsuarioRepository = FakeUsuarioRepository(),
        temaRepo: FakeTemaPreferenciasRepository = FakeTemaPreferenciasRepository(),
    ): MenuLateralPerfilViewModel {
        val cacheRepo = object : FakeUsuarioCacheRepository() {
            override fun observarPerfilCacheado() = perfilFlow
        }
        val context = mock<Context>()
        val limpiarDatos = mock<LimpiarTodosDatosLocalesUseCase>(lenient = true)
        return MenuLateralPerfilViewModel(
            getPerfilActualUseCase = GetPerfilActualUseCase(usuarioRepo),
            observarPerfilCacheadoUseCase = ObservarPerfilCacheadoUseCase(cacheRepo),
            observarModoTemaUseCase = ObservarModoTemaUseCase(temaRepo),
            establecerModoTemaUseCase = EstablecerModoTemaUseCase(temaRepo),
            cerrarSesionUseCase = CerrarSesionUseCase(FakeAuthRepository()),
            sincronizarFotoPerfilUseCase = SincronizarFotoPerfilUseCase(usuarioRepo),
            darDeBajaFcmTokenUseCase = DarDeBajaFcmTokenUseCase(FakeNotificacionesRepository()),
            limpiarTodosDatosLocalesUseCase = limpiarDatos,
            application = context,
        )
    }
}
