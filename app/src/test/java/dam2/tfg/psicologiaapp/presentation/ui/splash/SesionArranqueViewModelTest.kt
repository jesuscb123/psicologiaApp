package dam2.tfg.psicologiaapp.presentation.ui.splash

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dam2.tfg.psicologiaapp.auth.domain.usecase.CerrarSesionUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.RegistrarFcmTokenActualUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotificacionesRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GuardarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.LimpiarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObtenerPerfilCacheadoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SesionArranqueViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun sinUsuarioFirebase_navegaAIniciarSesion() = runTest {
        val firebaseAuth = mock<FirebaseAuth>()
        whenever(firebaseAuth.currentUser).thenReturn(null)

        val viewModel = crearViewModel(firebaseAuth = firebaseAuth)
        advanceUntilIdle()

        assertEquals(DestinoSesion.IniciarSesion, viewModel.uiState.value.destinoResuelto)
    }

    @Test
    fun conCachePaciente_navegaAGrafoPaciente() = runTest {
        val firebaseAuth = mockFirebaseConUid("uid-paciente")
        val cache = PerfilCacheado(1L, "uid-paciente", "Ana", "López", null, RolUsuario.PACIENTE, 2L)
        val cacheRepo = object : FakeUsuarioCacheRepository() {
            override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String) =
                if (firebaseUid == "uid-paciente") cache else null
        }

        val viewModel = crearViewModel(firebaseAuth = firebaseAuth, cacheRepo = cacheRepo)
        advanceUntilIdle()

        val destino = viewModel.uiState.value.destinoResuelto
        assertTrue(destino is DestinoSesion.Grafo)
        assertEquals(RolUsuario.PACIENTE, (destino as DestinoSesion.Grafo).rol)
    }

    @Test
    fun sinCache_yPerfilRedFallido_navegaAIniciarSesion() = runTest {
        val firebaseAuth = mockFirebaseConUid("uid-sin-cache")
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual() = Result.failure<UsuarioPerfil>(Exception("Sin red"))
        }

        val viewModel = crearViewModel(firebaseAuth = firebaseAuth, usuarioRepo = usuarioRepo)
        advanceUntilIdle()

        assertEquals(DestinoSesion.IniciarSesion, viewModel.uiState.value.destinoResuelto)
    }

    @Test
    fun alConsumirForzarLogin_limpiaBandera() = runTest {
        val viewModel = crearViewModel()
        viewModel.alConsumirForzarLogin()

        assertFalse(viewModel.uiState.value.forzarIrALogin)
    }

    private fun mockFirebaseConUid(uid: String): FirebaseAuth {
        val user = mock<FirebaseUser>()
        whenever(user.uid).thenReturn(uid)
        val firebaseAuth = mock<FirebaseAuth>()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        return firebaseAuth
    }

    private fun crearViewModel(
        firebaseAuth: FirebaseAuth = mock<FirebaseAuth>().also { whenever(it.currentUser).thenReturn(null) },
        cacheRepo: FakeUsuarioCacheRepository = FakeUsuarioCacheRepository(),
        usuarioRepo: FakeUsuarioRepository = FakeUsuarioRepository(),
    ): SesionArranqueViewModel {
        val authRepo = FakeAuthRepository()
        return SesionArranqueViewModel(
            firebaseAuth = firebaseAuth,
            obtenerPerfilCacheadoUseCase = ObtenerPerfilCacheadoUseCase(cacheRepo),
            getPerfilActualUseCase = GetPerfilActualUseCase(usuarioRepo),
            guardarPerfilCacheadoUseCase = GuardarPerfilCacheadoUseCase(cacheRepo),
            cerrarSesionUseCase = CerrarSesionUseCase(authRepo),
            limpiarPerfilCacheadoUseCase = LimpiarPerfilCacheadoUseCase(cacheRepo),
            registrarFcmTokenActualUseCase = RegistrarFcmTokenActualUseCase(FakeNotificacionesRepository()),
        )
    }
}
