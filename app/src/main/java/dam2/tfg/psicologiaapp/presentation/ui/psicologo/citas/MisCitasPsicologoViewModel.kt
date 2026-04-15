package dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObtenerMisCitasPsicologoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisCitasPsicologoViewModel @Inject constructor(
    private val obtenerMisCitasPsicologoUseCase: ObtenerMisCitasPsicologoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisCitasPsicologoUiState())
    val uiState: StateFlow<MisCitasPsicologoUiState> = _uiState

    fun recargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            obtenerMisCitasPsicologoUseCase().fold(
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
}

