package dam2.tfg.psicologiaapp.presentation.ui.paciente.perfilPsicologo

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.paciente.domain.usecase.AsignarPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakePacienteRepository
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilPsicologoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val psicologo = Psicologo(
        usuarioId = 10L,
        idEntidadPsicologo = 2L,
        firebaseUid = "uid-psi",
        nombre = "Dr",
        apellidos = "House",
        fotoPerfilUrl = null,
        numeroColegiado = "COL-1",
        especialidades = listOf("General"),
        descripcion = "Experto",
    )

    @Test
    fun cargar_idInvalido_muestraError() {
        val viewModel = crearViewModel()

        viewModel.cargar("no-es-numero")

        assertEquals("Id de psicólogo inválido", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun asignarPsicologo_sinPsicologoCargado_muestraError() {
        val viewModel = crearViewModel()

        viewModel.asignarPsicologo()

        assertEquals("No hay psicólogo para asignar", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun asignarPsicologo_exito_navega() = runTest {
        val psicologosFlow = MutableStateFlow(listOf(psicologo))
        val paciente = Paciente(1L, "uid-pac", "Ana", "López", null, null, 3L)
        val viewModel = crearViewModel(
            psicologosFlow = psicologosFlow,
            asignar = { Result.success(paciente.copy(psicologoId = 2L)) },
        )
        viewModel.cargar("2")
        advanceUntilIdle()

        viewModel.asignarPsicologo()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.asignando)
        assertEquals(EventoNavegacionPerfilPsicologo.AsignacionCompletada, estado.eventoNavegacion)
    }

    @Test
    fun observarPerfil_marcaSiYaTienePsicologo() = runTest {
        val perfilFlow = MutableStateFlow<PerfilCacheado?>(
            PerfilCacheado(1L, "uid", "Ana", "López", null, RolUsuario.PACIENTE, psicologoId = 2L),
        )
        val viewModel = crearViewModel(perfilFlow = perfilFlow)
        advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.pacienteYaTienePsicologo)
    }

    private fun crearViewModel(
        psicologosFlow: MutableStateFlow<List<Psicologo>> = MutableStateFlow(emptyList()),
        perfilFlow: MutableStateFlow<PerfilCacheado?> = MutableStateFlow(null),
        asignar: suspend (Long) -> Result<Paciente> = { Result.failure(NotImplementedError()) },
    ): PerfilPsicologoViewModel {
        val psicologoRepo = object : FakePsicologoRepository() {
            override fun observarPsicologos() = psicologosFlow
            override suspend fun listarPsicologos() = Result.success(psicologosFlow.value)
        }
        val pacienteRepo = object : FakePacienteRepository() {
            override suspend fun asignarPsicologo(psicologoId: Long) = asignar(psicologoId)
        }
        val cacheRepo = object : FakeUsuarioCacheRepository() {
            override fun observarPerfilCacheado() = perfilFlow
        }
        return PerfilPsicologoViewModel(
            ObservarPsicologosUseCase(psicologoRepo),
            SincronizarPsicologosUseCase(psicologoRepo),
            AsignarPsicologoUseCase(pacienteRepo),
            ObservarPerfilCacheadoUseCase(cacheRepo),
        )
    }
}
