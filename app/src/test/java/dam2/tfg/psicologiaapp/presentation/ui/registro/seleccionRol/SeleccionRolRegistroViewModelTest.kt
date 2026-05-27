package dam2.tfg.psicologiaapp.presentation.ui.registro.seleccionRol

import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeleccionRolRegistroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun elegirPaciente_navegaARegistroPaciente() {
        val viewModel = SeleccionRolRegistroViewModel()

        viewModel.elegirPaciente()

        assertEquals(EventoNavegacionSeleccionRol.IrARegistroPaciente, viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun elegirPsicologo_navegaARegistroPsicologo() {
        val viewModel = SeleccionRolRegistroViewModel()

        viewModel.elegirPsicologo()

        assertEquals(EventoNavegacionSeleccionRol.IrARegistroPsicologo, viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun volver_emiteEventoVolver() {
        val viewModel = SeleccionRolRegistroViewModel()

        viewModel.volver()

        assertEquals(EventoNavegacionSeleccionRol.Volver, viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun alConsumirEventoNavegacion_limpiaEvento() {
        val viewModel = SeleccionRolRegistroViewModel()
        viewModel.elegirPaciente()

        viewModel.alConsumirEventoNavegacion()

        assertNull(viewModel.uiState.value.eventoNavegacion)
    }
}
