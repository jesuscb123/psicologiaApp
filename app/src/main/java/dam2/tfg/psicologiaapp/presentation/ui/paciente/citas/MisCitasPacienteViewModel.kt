package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.usecase.CancelarCitaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasGrafoPaciente
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisCitasPacienteViewModel @Inject constructor(
    private val observarMisCitasPacienteUseCase: ObservarMisCitasPacienteUseCase,
    private val sincronizarMisCitasPacienteUseCase: SincronizarMisCitasPacienteUseCase,
    private val cancelarCitaUseCase: CancelarCitaUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val filtroInicial = when (savedStateHandle.get<String>(RutasGrafoPaciente.ARG_FILTRO_CITAS)) {
        "finalizadas" -> FiltroMisCitas.FINALIZADAS
        else -> FiltroMisCitas.ACTIVAS
    }

    private val _uiState = MutableStateFlow(MisCitasPacienteUiState(filtroSeleccionado = filtroInicial))
    val uiState: StateFlow<MisCitasPacienteUiState> = _uiState

    init {
        viewModelScope.launch {
            observarMisCitasPacienteUseCase().collectLatest { lista ->
                _uiState.update { it.copy(citas = lista) }
            }
        }
    }

    fun recargar() {
        viewModelScope.launch {
            val hayDatos = _uiState.value.citas.isNotEmpty()
            if (!hayDatos) {
                _uiState.update { it.copy(cargando = true, mensajeError = null) }
            }
            sincronizarMisCitasPacienteUseCase().fold(
                onSuccess = { _uiState.update { it.copy(cargando = false, mensajeError = null) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
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
                onSuccess = { _uiState.update { it.copy(cargando = false) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cancelar la cita",
                        )
                    }
                }
            )
        }
    }

    fun cambiarFiltro(filtro: FiltroMisCitas) {
        _uiState.update { it.copy(filtroSeleccionado = filtro) }
    }

    fun puedeCancelar(estado: EstadoCitaCalculado): Boolean =
        estado == EstadoCitaCalculado.ACTIVA
}
