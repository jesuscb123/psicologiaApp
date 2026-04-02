package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.nota.domain.usecase.CrearNotaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnadirNotaViewModel @Inject constructor(
    private val crearNotaUseCase: CrearNotaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnadirNotaUiState())
    val uiState: StateFlow<AnadirNotaUiState> = _uiState

    fun alCambiarAsunto(nuevoAsunto: String) {
        _uiState.update { it.copy(asunto = nuevoAsunto, mensajeError = null) }
    }

    fun alCambiarDescripcion(nuevaDescripcion: String) {
        _uiState.update { it.copy(descripcion = nuevaDescripcion, mensajeError = null) }
    }

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }

    fun guardarNota() {
        val asunto = uiState.value.asunto.trim()
        val descripcion = uiState.value.descripcion.trim()

        if (asunto.isBlank() || descripcion.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Rellena asunto y descripción") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val resultado = crearNotaUseCase(asunto = asunto, descripcion = descripcion)
            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            eventoNavegacion = EventoNavegacionAnadirNota.NotaGuardada
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo guardar la nota"
                        )
                    }
                }
            )
        }
    }
}

