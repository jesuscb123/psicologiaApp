package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SeleccionRolRegistroViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SeleccionRolRegistroUiState())
    val uiState: StateFlow<SeleccionRolRegistroUiState> = _uiState

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }

    fun elegirPaciente() {
        _uiState.update { it.copy(eventoNavegacion = EventoNavegacionSeleccionRol.IrARegistroPaciente) }
    }

    fun elegirPsicologo() {
        _uiState.update { it.copy(eventoNavegacion = EventoNavegacionSeleccionRol.IrARegistroPsicologo) }
    }

    fun volver() {
        _uiState.update { it.copy(eventoNavegacion = EventoNavegacionSeleccionRol.Volver) }
    }
}

