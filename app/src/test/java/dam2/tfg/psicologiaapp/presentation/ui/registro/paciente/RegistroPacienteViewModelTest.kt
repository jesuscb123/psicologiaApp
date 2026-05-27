package dam2.tfg.psicologiaapp.presentation.ui.registro.paciente

import dam2.tfg.psicologiaapp.auth.domain.usecase.CrearCuentaUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.EliminarUsuarioFirebaseActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.UsuarioPaciente
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.usecase.CrearUsuarioUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroPacienteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun alCambiarCorreo_actualizaEstado() {
        val viewModel = crearViewModel()

        viewModel.alCambiarCorreo("paciente@test.com")

        assertEquals("paciente@test.com", viewModel.uiState.value.correo)
    }

    @Test
    fun registrarPaciente_camposVacios_muestraError() {
        val viewModel = crearViewModel()

        viewModel.registrarPaciente()

        assertEquals("Rellena todos los campos", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun registrarPaciente_exito_marcaCompletado() = runTest {
        val viewModel = crearViewModel(
            crearCuenta = { _, _ -> Result.success("firebase-uid") },
            crearUsuario = { _ ->
                Result.success(
                    UsuarioPaciente(1L, "firebase-uid", "Ana", "López", null, RolUsuario.PACIENTE, null, 10L),
                )
            },
        )
        rellenarCampos(viewModel)

        viewModel.registrarPaciente()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertTrue(estado.registroCompletado)
        assertEquals(null, estado.mensajeError)
    }

    @Test
    fun registrarPaciente_falloCrearCuenta_muestraError() = runTest {
        val viewModel = crearViewModel(
            crearCuenta = { _, _ -> Result.failure(Exception("Correo en uso")) },
        )
        rellenarCampos(viewModel)

        viewModel.registrarPaciente()
        advanceUntilIdle()

        assertEquals("Correo en uso", viewModel.uiState.value.mensajeError)
        assertFalse(viewModel.uiState.value.registroCompletado)
    }

    @Test
    fun registrarPaciente_falloCrearUsuario_haceRollback() = runTest {
        val viewModel = crearViewModel(
            crearCuenta = { _, _ -> Result.success("firebase-uid") },
            crearUsuario = { _ -> Result.failure(Exception("Error backend")) },
        )
        rellenarCampos(viewModel)

        viewModel.registrarPaciente()
        advanceUntilIdle()

        assertEquals("Error backend", viewModel.uiState.value.mensajeError)
        assertFalse(viewModel.uiState.value.registroCompletado)
    }

    private fun rellenarCampos(viewModel: RegistroPacienteViewModel) {
        viewModel.alCambiarCorreo("paciente@test.com")
        viewModel.alCambiarContrasena("password123")
        viewModel.alCambiarNombre("Ana")
        viewModel.alCambiarApellidos("López")
    }

    private fun crearViewModel(
        crearCuenta: suspend (String, String) -> Result<String> = { _, _ -> Result.failure(NotImplementedError()) },
        crearUsuario: suspend (UsuarioRequest) -> Result<Usuario> = { _ -> Result.failure(NotImplementedError()) },
    ): RegistroPacienteViewModel {
        val authRepo = object : FakeAuthRepository() {
            override suspend fun crearCuenta(correo: String, contrasena: String) = crearCuenta(correo, contrasena)
        }
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun crearUsuario(request: UsuarioRequest) = crearUsuario(request)
        }
        return RegistroPacienteViewModel(
            CrearCuentaUseCase(authRepo),
            CrearUsuarioUseCase(usuarioRepo),
            EliminarUsuarioFirebaseActualUseCase(authRepo),
        )
    }
}
