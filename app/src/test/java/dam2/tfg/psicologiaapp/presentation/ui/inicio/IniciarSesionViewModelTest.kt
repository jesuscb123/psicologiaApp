package dam2.tfg.psicologiaapp.presentation.ui.inicio

import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import dam2.tfg.psicologiaapp.auth.domain.usecase.IniciarSesionUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.SolicitarRestablecerContrasenaUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.RegistrarFcmTokenActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.VerificarExistenciaCorreoUseCase
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class IniciarSesionViewModelTest {

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
    fun solicitarRecuperacionContrasena_muestraToastExito_yCierraDialogo_cuandoEsExito() = runTest {
        val viewModel = crearViewModel(
            resultadoVerificacionCorreo = { Result.success(true) },
            resultadoRecuperacion = { Result.success(Unit) }
        )

        viewModel.abrirDialogoRecuperacion()
        viewModel.alCambiarCorreoRecuperacion("paciente@correo.com")

        viewModel.solicitarRecuperacionContrasena()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.mostrandoDialogoRecuperacion)
        assertFalse(estado.cargandoRecuperacion)
        assertEquals(MENSAJE_EXITO_RECUPERACION, estado.mensajeInfoRecuperacion)
        assertNull(estado.mensajeErrorRecuperacion)
        assertEquals("", estado.correoRecuperacion)
    }

    @Test
    fun solicitarRecuperacionContrasena_muestraToastNoExisteCorreo_yMantieneDialogo_cuandoNoExisteUsuario() = runTest {
        var seInvocoFirebase = false
        val viewModel = crearViewModel(
            resultadoVerificacionCorreo = { Result.success(false) },
            resultadoRecuperacion = {
                seInvocoFirebase = true
                Result.success(Unit)
            }
        )

        viewModel.abrirDialogoRecuperacion()
        viewModel.alCambiarCorreoRecuperacion("desconocido@correo.com")

        viewModel.solicitarRecuperacionContrasena()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.mostrandoDialogoRecuperacion)
        assertFalse(estado.cargandoRecuperacion)
        assertEquals(MENSAJE_ERROR_USUARIO_NO_EXISTE, estado.mensajeInfoRecuperacion)
        assertNull(estado.mensajeErrorRecuperacion)
        assertEquals("desconocido@correo.com", estado.correoRecuperacion)
        assertFalse(seInvocoFirebase)
    }

    @Test
    fun solicitarRecuperacionContrasena_muestraToastError_cuandoFallaPorIOException() = runTest {
        val viewModel = crearViewModel(
            resultadoVerificacionCorreo = { Result.success(true) },
            resultadoRecuperacion = { Result.failure(IOException("Sin internet")) }
        )

        viewModel.abrirDialogoRecuperacion()
        viewModel.alCambiarCorreoRecuperacion("paciente@correo.com")

        viewModel.solicitarRecuperacionContrasena()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.mostrandoDialogoRecuperacion)
        assertFalse(estado.cargandoRecuperacion)
        assertNull(estado.mensajeErrorRecuperacion)
        assertEquals(MENSAJE_ERROR_RED_RECUPERACION, estado.mensajeInfoRecuperacion)
        assertEquals("paciente@correo.com", estado.correoRecuperacion)
    }

    @Test
    fun solicitarRecuperacionContrasena_muestraToastError_cuandoFallaVerificacionConBackend() = runTest {
        val viewModel = crearViewModel(
            resultadoVerificacionCorreo = { Result.failure(IOException("Backend caido")) },
            resultadoRecuperacion = { Result.success(Unit) }
        )

        viewModel.abrirDialogoRecuperacion()
        viewModel.alCambiarCorreoRecuperacion("paciente@correo.com")

        viewModel.solicitarRecuperacionContrasena()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.mostrandoDialogoRecuperacion)
        assertFalse(estado.cargandoRecuperacion)
        assertNull(estado.mensajeErrorRecuperacion)
        assertEquals(MENSAJE_ERROR_VERIFICAR_CORREO_BACKEND, estado.mensajeInfoRecuperacion)
    }

    private fun crearViewModel(
        resultadoVerificacionCorreo: suspend (String) -> Result<Boolean>,
        resultadoRecuperacion: suspend (String) -> Result<Unit>
    ): IniciarSesionViewModel {
        val authRepository = FakeAuthRepository(resultadoRecuperacion)
        val usuarioRepository = FakeUsuarioRepository(resultadoVerificacionCorreo)
        val notificacionesRepository = FakeNotificacionesRepository()
        return IniciarSesionViewModel(
            iniciarSesionUseCase = IniciarSesionUseCase(authRepository),
            getPerfilActualUseCase = GetPerfilActualUseCase(usuarioRepository),
            solicitarRestablecerContrasenaUseCase = SolicitarRestablecerContrasenaUseCase(authRepository),
            verificarExistenciaCorreoUseCase = VerificarExistenciaCorreoUseCase(usuarioRepository),
            registrarFcmTokenActualUseCase = RegistrarFcmTokenActualUseCase(notificacionesRepository),
        )
    }

    private class FakeNotificacionesRepository : NotificacionesRepository {
        override suspend fun registrarTokenActual(token: String): Result<Unit> = Result.success(Unit)
        override suspend fun darDeBajaToken(token: String): Result<Unit> = Result.success(Unit)
        override suspend fun obtenerTokenFcmActual(): Result<String> = Result.success("test-token")
        override suspend fun notificarMensajeChat(
            chatId: String,
            vistaPreviaTexto: String,
        ): Result<Unit> = Result.success(Unit)
    }

    private class FakeAuthRepository(
        private val resultadoRecuperacion: suspend (String) -> Result<Unit>
    ) : AuthRepository {
        override suspend fun iniciarSesion(correo: String, contrasena: String): Result<String> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun crearCuenta(correo: String, contrasena: String): Result<String> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun eliminarUsuarioActual(): Result<Unit> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun cerrarSesion(): Result<Unit> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun solicitarRestablecerContrasena(correo: String): Result<Unit> =
            resultadoRecuperacion(correo)

        override suspend fun forzarRenovacionTokenIdentidad() = Unit
    }

    private class FakeUsuarioRepository(
        private val resultadoVerificacionCorreo: suspend (String) -> Result<Boolean>
    ) : UsuarioRepository {
        override suspend fun existeCorreo(email: String): Result<Boolean> =
            resultadoVerificacionCorreo(email)

        override suspend fun getPerfilActual(): Result<UsuarioPerfil> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<UsuarioPerfil> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun borrarUsuario(): Result<Unit> =
            Result.failure(NotImplementedError("No se usa en este test"))

        override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario> =
            Result.failure(NotImplementedError("No se usa en este test"))
    }

    companion object {
        private const val MENSAJE_EXITO_RECUPERACION =
            "Correo enviado correctamente. Revisa tu bandeja y también spam."
        private const val MENSAJE_ERROR_USUARIO_NO_EXISTE =
            "El correo no existe en Firebase. No se puede modificar la contraseña."
        private const val MENSAJE_ERROR_RED_RECUPERACION =
            "No se pudo enviar el correo por un problema de red. Inténtalo de nuevo."
        private const val MENSAJE_ERROR_VERIFICAR_CORREO_BACKEND =
            "No se pudo verificar el correo en el servidor. Inténtalo de nuevo."
    }
}
