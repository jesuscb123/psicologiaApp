package dam2.tfg.psicologiaapp.presentation.ui.inicio

import dam2.tfg.psicologiaapp.auth.domain.usecase.IniciarSesionUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.SolicitarRestablecerContrasenaUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.RegistrarFcmTokenActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotificacionesRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
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
    fun alCambiarCorreo_yContrasena_actualizanEstado() {
        val viewModel = crearViewModel()

        viewModel.alCambiarCorreo("user@test.com")
        viewModel.alCambiarContrasena("secret")

        assertEquals("user@test.com", viewModel.uiState.value.correo)
        assertEquals("secret", viewModel.uiState.value.contrasena)
    }

    @Test
    fun iniciarSesion_camposVacios_muestraError() {
        val viewModel = crearViewModel()

        viewModel.iniciarSesion()

        assertEquals("Rellena correo y contraseña", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesion_paciente_navegaAHomePaciente() = runTest {
        val viewModel = crearViewModel(
            iniciarSesion = { _, _ -> Result.success("uid") },
            getPerfil = { Result.success(perfilPaciente()) },
        )
        viewModel.alCambiarCorreo("paciente@test.com")
        viewModel.alCambiarContrasena("pass")

        viewModel.iniciarSesion()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(EventoNavegacionIniciarSesion.IrAHomePaciente, estado.eventoNavegacion)
        assertEquals(false, estado.cargando)
    }

    @Test
    fun iniciarSesion_psicologo_navegaAHomePsicologo() = runTest {
        val viewModel = crearViewModel(
            iniciarSesion = { _, _ -> Result.success("uid") },
            getPerfil = { Result.success(perfilPsicologo()) },
        )
        viewModel.alCambiarCorreo("psi@test.com")
        viewModel.alCambiarContrasena("pass")

        viewModel.iniciarSesion()
        advanceUntilIdle()

        assertEquals(EventoNavegacionIniciarSesion.IrAHomePsicologo, viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun iniciarSesion_falloLogin_muestraErrorCredenciales() = runTest {
        val viewModel = crearViewModel(
            iniciarSesion = { _, _ -> Result.failure(Exception("INVALID_LOGIN")) },
        )
        viewModel.alCambiarCorreo("user@test.com")
        viewModel.alCambiarContrasena("wrong")

        viewModel.iniciarSesion()
        advanceUntilIdle()

        assertEquals(MENSAJE_ERROR_CREDENCIALES_LOGIN, viewModel.uiState.value.mensajeError)
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

    private fun perfilPaciente() = PacientePerfil(
        usuarioId = 1L,
        firebaseUid = "uid",
        nombre = "Ana",
        apellidos = "López",
        email = "paciente@test.com",
        fotoPerfilUrl = null,
        psicologoId = 2L,
    )

    private fun perfilPsicologo() = PsicologoPerfil(
        usuarioId = 2L,
        firebaseUid = "uid-psi",
        nombre = "Dr",
        apellidos = "House",
        email = "psi@test.com",
        fotoPerfilUrl = null,
        numeroColegiado = "COL-1",
        especialidades = listOf("General"),
        descripcion = null,
    )

    private fun crearViewModel(
        iniciarSesion: suspend (String, String) -> Result<String> = { _, _ ->
            Result.failure(NotImplementedError())
        },
        getPerfil: suspend () -> Result<UsuarioPerfil> = { Result.failure(NotImplementedError()) },
        resultadoVerificacionCorreo: suspend (String) -> Result<Boolean> = { Result.success(true) },
        resultadoRecuperacion: suspend (String) -> Result<Unit> = { Result.success(Unit) },
    ): IniciarSesionViewModel {
        val authRepository = object : FakeAuthRepository() {
            override suspend fun iniciarSesion(correo: String, contrasena: String) =
                iniciarSesion(correo, contrasena)
            override suspend fun solicitarRestablecerContrasena(correo: String) =
                resultadoRecuperacion(correo)
        }
        val usuarioRepository = object : FakeUsuarioRepository() {
            override suspend fun existeCorreo(email: String) = resultadoVerificacionCorreo(email)
            override suspend fun getPerfilActual() = getPerfil()
        }
        return IniciarSesionViewModel(
            iniciarSesionUseCase = IniciarSesionUseCase(authRepository),
            getPerfilActualUseCase = GetPerfilActualUseCase(usuarioRepository),
            solicitarRestablecerContrasenaUseCase = SolicitarRestablecerContrasenaUseCase(authRepository),
            verificarExistenciaCorreoUseCase = VerificarExistenciaCorreoUseCase(usuarioRepository),
            registrarFcmTokenActualUseCase = RegistrarFcmTokenActualUseCase(FakeNotificacionesRepository()),
        )
    }

    companion object {
        private const val MENSAJE_ERROR_CREDENCIALES_LOGIN = "Correo o contraseña incorrectos."
        private const val MENSAJE_EXITO_RECUPERACION =
            "Correo enviado correctamente. Revisa tu bandeja y también spam."
        private const val MENSAJE_ERROR_USUARIO_NO_EXISTE =
            "Si el correo está registrado, recibirás un enlace de recuperación."
        private const val MENSAJE_ERROR_RED_RECUPERACION =
            "No se pudo enviar el correo por un problema de red. Inténtalo de nuevo."
        private const val MENSAJE_ERROR_VERIFICAR_CORREO_BACKEND =
            "No se pudo verificar el correo en el servidor. Inténtalo de nuevo."
    }
}
