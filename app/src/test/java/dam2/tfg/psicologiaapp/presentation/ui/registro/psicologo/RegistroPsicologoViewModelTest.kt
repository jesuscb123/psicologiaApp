package dam2.tfg.psicologiaapp.presentation.ui.registro.psicologo

import dam2.tfg.psicologiaapp.auth.domain.usecase.CrearCuentaUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.EliminarUsuarioFirebaseActualUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.model.UsuarioPsicologo
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
class RegistroPsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun alAnadirEspecialidad_vacia_muestraError() {
        val viewModel = crearViewModel()

        viewModel.alAnadirEspecialidad()

        assertEquals("Escribe una especialidad antes de añadir", viewModel.uiState.value.errorEspecialidadInput)
    }

    @Test
    fun alAnadirEspecialidad_valida_laAgregaALista() {
        val viewModel = crearViewModel()
        viewModel.alCambiarEspecialidadInput("Ansiedad")

        viewModel.alAnadirEspecialidad()

        assertEquals(listOf("Ansiedad"), viewModel.uiState.value.especialidades)
        assertEquals("", viewModel.uiState.value.especialidadInput)
    }

    @Test
    fun registrarPsicologo_sinEspecialidades_muestraError() {
        val viewModel = crearViewModel()
        rellenarCampos(viewModel)

        viewModel.registrarPsicologo()

        assertEquals("Rellena todos los campos", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun registrarPsicologo_exito_marcaCompletado() = runTest {
        val viewModel = crearViewModel(
            crearCuenta = { _, _ -> Result.success("firebase-uid") },
            crearUsuario = { _ ->
                Result.success(
                    UsuarioPsicologo(1L, "firebase-uid", "Dr", "House", null, RolUsuario.PSICOLOGO, "COL-1", listOf("General"), null),
                )
            },
        )
        rellenarCampos(viewModel)
        viewModel.alCambiarEspecialidadInput("General")
        viewModel.alAnadirEspecialidad()

        viewModel.registrarPsicologo()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertTrue(estado.registroCompletado)
    }

    @Test
    fun registrarPsicologo_falloCrearCuenta_muestraError() = runTest {
        val viewModel = crearViewModel(
            crearCuenta = { _, _ -> Result.failure(Exception("Error Firebase")) },
        )
        rellenarCampos(viewModel)
        viewModel.alCambiarEspecialidadInput("General")
        viewModel.alAnadirEspecialidad()

        viewModel.registrarPsicologo()
        advanceUntilIdle()

        assertEquals("Error Firebase", viewModel.uiState.value.mensajeError)
    }

    private fun rellenarCampos(viewModel: RegistroPsicologoViewModel) {
        viewModel.alCambiarCorreo("psi@test.com")
        viewModel.alCambiarContrasena("password123")
        viewModel.alCambiarNombre("Dr")
        viewModel.alCambiarApellidos("House")
        viewModel.alCambiarNumeroColegiado("COL-12345")
    }

    private fun crearViewModel(
        crearCuenta: suspend (String, String) -> Result<String> = { _, _ -> Result.failure(NotImplementedError()) },
        crearUsuario: suspend (UsuarioRequest) -> Result<Usuario> = { _ -> Result.failure(NotImplementedError()) },
    ): RegistroPsicologoViewModel {
        val authRepo = object : FakeAuthRepository() {
            override suspend fun crearCuenta(correo: String, contrasena: String) = crearCuenta(correo, contrasena)
        }
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun crearUsuario(request: UsuarioRequest) = crearUsuario(request)
        }
        return RegistroPsicologoViewModel(
            CrearCuentaUseCase(authRepo),
            CrearUsuarioUseCase(usuarioRepo),
            EliminarUsuarioFirebaseActualUseCase(authRepo),
        )
    }
}
