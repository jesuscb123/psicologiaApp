package dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ActualizarDescripcionPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ActualizarEspecialidadesPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AjustesPsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val psicologo = Psicologo(
        usuarioId = 5L,
        idEntidadPsicologo = 2L,
        firebaseUid = "uid-psi",
        nombre = "Dr",
        apellidos = "House",
        fotoPerfilUrl = null,
        numeroColegiado = "COL-1",
        especialidades = listOf("Ansiedad"),
        descripcion = "Descripción inicial",
    )

    @Test
    fun init_cargaDatosDesdeRoom() = runTest {
        val viewModel = crearViewModel(
            perfilFlow = MutableStateFlow(PerfilCacheado(5L, "uid", "Dr", "House", null, RolUsuario.PSICOLOGO)),
            psicologosFlow = MutableStateFlow(listOf(psicologo)),
        )
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.yaSeHaCargado)
        assertEquals("Descripción inicial", estado.descripcion)
        assertEquals(listOf("Ansiedad"), estado.especialidades)
    }

    @Test
    fun alAnadirEspecialidad_duplicada_muestraError() = runTest {
        val viewModel = crearViewModel(
            perfilFlow = MutableStateFlow(PerfilCacheado(5L, "uid", "Dr", "House", null, RolUsuario.PSICOLOGO)),
            psicologosFlow = MutableStateFlow(listOf(psicologo)),
        )
        advanceUntilIdle()

        viewModel.alCambiarEspecialidadInput("Ansiedad")
        viewModel.alAnadirEspecialidad()

        assertEquals("Ya existe esa especialidad", viewModel.uiState.value.errorEspecialidadInput)
    }

    @Test
    fun guardar_sinCambios_noHaceNada() = runTest {
        val viewModel = crearViewModel(
            perfilFlow = MutableStateFlow(PerfilCacheado(5L, "uid", "Dr", "House", null, RolUsuario.PSICOLOGO)),
            psicologosFlow = MutableStateFlow(listOf(psicologo)),
        )
        advanceUntilIdle()

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals(null, viewModel.uiState.value.mensajeOk)
    }

    @Test
    fun guardar_cambioDescripcion_muestraExito() = runTest {
        val viewModel = crearViewModel(
            perfilFlow = MutableStateFlow(PerfilCacheado(5L, "uid", "Dr", "House", null, RolUsuario.PSICOLOGO)),
            psicologosFlow = MutableStateFlow(listOf(psicologo)),
            actualizarDescripcion = { Result.success(psicologo.copy(descripcion = it)) },
        )
        advanceUntilIdle()

        viewModel.alCambiarDescripcion("Nueva descripción")
        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Cambios guardados", viewModel.uiState.value.mensajeOk)
    }

    @Test
    fun limpiarMensajes_borraFeedback() = runTest {
        val viewModel = crearViewModel()
        viewModel.limpiarMensajes()

        assertEquals(null, viewModel.uiState.value.mensajeError)
        assertEquals(null, viewModel.uiState.value.mensajeOk)
    }

    private fun crearViewModel(
        perfilFlow: MutableStateFlow<PerfilCacheado?> = MutableStateFlow(null),
        psicologosFlow: MutableStateFlow<List<Psicologo>> = MutableStateFlow(emptyList()),
        actualizarDescripcion: suspend (String?) -> Result<Psicologo> = { Result.failure(NotImplementedError()) },
        actualizarEspecialidades: suspend (List<String>) -> Result<Psicologo> = { Result.failure(NotImplementedError()) },
    ): AjustesPsicologoViewModel {
        val psicologoRepo = object : FakePsicologoRepository() {
            override fun observarPsicologos() = psicologosFlow
            override suspend fun listarPsicologos() = Result.success(psicologosFlow.value)
            override suspend fun actualizarMiDescripcion(descripcion: String?) = actualizarDescripcion(descripcion)
            override suspend fun actualizarMisEspecialidades(especialidades: List<String>) =
                actualizarEspecialidades(especialidades)
        }
        val cacheRepo = object : FakeUsuarioCacheRepository() {
            override fun observarPerfilCacheado() = perfilFlow
        }
        val usuarioRepo = FakeUsuarioRepository()
        return AjustesPsicologoViewModel(
            observarPerfilCacheadoUseCase = ObservarPerfilCacheadoUseCase(cacheRepo),
            observarPsicologosUseCase = ObservarPsicologosUseCase(psicologoRepo),
            sincronizarPsicologosUseCase = SincronizarPsicologosUseCase(psicologoRepo),
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(usuarioRepo, cacheRepo),
            actualizarDescripcionPsicologoUseCase = ActualizarDescripcionPsicologoUseCase(psicologoRepo),
            actualizarEspecialidadesPsicologoUseCase = ActualizarEspecialidadesPsicologoUseCase(psicologoRepo),
        )
    }
}
