package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.nota.domain.usecase.GetNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.GetPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.GetTareasDePacienteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FichaPacientePsicologoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPacientesDePsicologoUseCase: GetPacientesDePsicologoUseCase,
    private val getNotasDePacienteUseCase: GetNotasDePacienteUseCase,
    private val getTareasDePacienteUseCase: GetTareasDePacienteUseCase,
) : ViewModel() {

    private val pacienteId: Long = savedStateHandle.get<Long>(RutasApp.ARG_PACIENTE_ID) ?: 0L

    private val _uiState = MutableStateFlow(FichaPacientePsicologoUiState())
    val uiState: StateFlow<FichaPacientePsicologoUiState> = _uiState

    fun cambiarPestana(pestana: PestanaFichaPacientePsi) {
        _uiState.update { it.copy(pestanaActual = pestana) }
    }

    fun recargar() {
        if (pacienteId == 0L) {
            _uiState.update {
                it.copy(
                    cargando = false,
                    mensajeError = "Identificador de paciente no válido",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val pacienteEnLista = getPacientesDePsicologoUseCase().fold(
                onSuccess = { lista -> lista.find { it.idPaciente == pacienteId } },
                onFailure = { null },
            )
            val nombrePaciente = pacienteEnLista?.nombreUsuario.orEmpty()
            val fotoPaciente = pacienteEnLista?.fotoPerfilUrl

            val notas = getNotasDePacienteUseCase(pacienteId).fold(
                onSuccess = { it },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudieron cargar las notas",
                        )
                    }
                    return@launch
                },
            )

            val tareas = getTareasDePacienteUseCase(pacienteId).fold(
                onSuccess = { it },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudieron cargar las tareas",
                        )
                    }
                    return@launch
                },
            )

            _uiState.update {
                it.copy(
                    cargando = false,
                    nombreUsuarioPaciente = nombrePaciente,
                    fotoPerfilUrlPaciente = fotoPaciente,
                    notas = notas,
                    tareas = tareas,
                    mensajeError = null,
                )
            }
        }
    }
}
