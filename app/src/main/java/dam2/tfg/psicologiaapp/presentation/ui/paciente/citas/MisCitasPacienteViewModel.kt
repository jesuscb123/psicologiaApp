package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.usecase.CancelarCitaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObtenerMisCitasPacienteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisCitasPacienteViewModel @Inject constructor(
    private val obtenerMisCitasPacienteUseCase: ObtenerMisCitasPacienteUseCase,
    private val cancelarCitaUseCase: CancelarCitaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisCitasPacienteUiState())
    val uiState: StateFlow<MisCitasPacienteUiState> = _uiState

    fun recargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            obtenerMisCitasPacienteUseCase().fold(
                onSuccess = { lista ->
                    _uiState.update { it.copy(cargando = false, citas = lista, mensajeError = null) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            citas = emptyList(),
                            mensajeError = error.message ?: "No se pudieron cargar las citas",
                        )
                    }
                }
            )
        }
    }

    fun cancelarCita(citaId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            cancelarCitaUseCase(citaId).fold(
                onSuccess = {
                    _uiState.update { it.copy(cargando = false) }
                    recargar()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cancelar la cita",
                        )
                    }
                    recargar()
                }
            )
        }
    }

    fun puedeCancelar(estado: EstadoCitaCalculado): Boolean =
        estado == EstadoCitaCalculado.ACTIVA
}

