package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.paciente.domain.usecase.AsignarPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ListarPsicologosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilPsicologoViewModel @Inject constructor(
    private val listarPsicologosUseCase: ListarPsicologosUseCase,
    private val asignarPsicologoUseCase: AsignarPsicologoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilPsicologoUiState())
    val uiState: StateFlow<PerfilPsicologoUiState> = _uiState

    fun cargar(psicologoId: String) {
        val idLong = psicologoId.toLongOrNull()
        if (idLong == null) {
            _uiState.update { it.copy(mensajeError = "Id de psicólogo inválido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val resultado = listarPsicologosUseCase()
            resultado.fold(
                onSuccess = { lista ->
                    val psicologo = lista.firstOrNull { it.usuarioId == idLong }
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            psicologo = psicologo,
                            mensajeError = if (psicologo == null) "No se encontró el psicólogo" else null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el psicólogo"
                        )
                    }
                }
            )
        }
    }

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }

    fun asignarPsicologo() {
        val psicologoId = uiState.value.psicologo?.idEntidadPsicologo
        if (psicologoId == null) {
            _uiState.update { it.copy(mensajeError = "No hay psicólogo para asignar") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(asignando = true, mensajeError = null) }
            val resultado = asignarPsicologoUseCase(psicologoId)
            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            asignando = false,
                            eventoNavegacion = EventoNavegacionPerfilPsicologo.AsignacionCompletada
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            asignando = false,
                            mensajeError = error.message ?: "No se pudo asignar el psicólogo"
                        )
                    }
                }
            )
        }
    }
}

