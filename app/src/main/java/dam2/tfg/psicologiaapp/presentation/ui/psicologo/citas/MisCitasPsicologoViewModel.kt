package dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisCitasPsicologoViewModel @Inject constructor(
    private val observarMisCitasPsicologoUseCase: ObservarMisCitasPsicologoUseCase,
    private val sincronizarMisCitasPsicologoUseCase: SincronizarMisCitasPsicologoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisCitasPsicologoUiState())
    val uiState: StateFlow<MisCitasPsicologoUiState> = _uiState

    init {
        viewModelScope.launch {
            observarMisCitasPsicologoUseCase().collectLatest { lista ->
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
            sincronizarMisCitasPsicologoUseCase().fold(
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

    fun cambiarFiltro(filtro: FiltroMisCitas) {
        _uiState.update { it.copy(filtroSeleccionado = filtro) }
    }
}
