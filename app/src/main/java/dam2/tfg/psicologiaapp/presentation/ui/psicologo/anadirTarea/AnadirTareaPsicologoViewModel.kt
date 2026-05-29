package dam2.tfg.psicologiaapp.presentation.ui.psicologo.anadirTarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.tarea.domain.LimitesCaracteresTarea
import dam2.tfg.psicologiaapp.tarea.domain.usecase.CrearTareaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnadirTareaPsicologoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val crearTareaUseCase: CrearTareaUseCase,
) : ViewModel() {

    private val pacienteId: Long = savedStateHandle.get<Long>(RutasApp.ARG_PACIENTE_ID) ?: 0L

    private val _uiState = MutableStateFlow(AnadirTareaPsicologoUiState())
    val uiState: StateFlow<AnadirTareaPsicologoUiState> = _uiState

    fun alCambiarTitulo(nuevoTitulo: String) {
        if (nuevoTitulo.length <= LimitesCaracteresTarea.TITULO) {
            _uiState.update { it.copy(titulo = nuevoTitulo, mensajeError = null) }
        }
    }

    fun alCambiarDescripcion(nuevaDescripcion: String) {
        if (nuevaDescripcion.length <= LimitesCaracteresTarea.DESCRIPCION) {
            _uiState.update { it.copy(descripcion = nuevaDescripcion, mensajeError = null) }
        }
    }

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }

    fun guardarTarea() {
        val titulo = uiState.value.titulo.trim()
        val descripcion = uiState.value.descripcion.trim()

        if (pacienteId == 0L) {
            _uiState.update { it.copy(mensajeError = "Identificador de paciente no válido") }
            return
        }

        if (titulo.isBlank() || descripcion.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Rellena título y descripción") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val resultado = crearTareaUseCase(
                pacienteId = pacienteId,
                titulo = titulo,
                descripcion = descripcion,
            )
            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            eventoNavegacion = EventoNavegacionAnadirTareaPsicologo.TareaGuardada,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo guardar la tarea",
                        )
                    }
                },
            )
        }
    }
}
