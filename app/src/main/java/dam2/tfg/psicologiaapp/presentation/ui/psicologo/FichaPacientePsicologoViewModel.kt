package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.GetPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.resumenIa.domain.usecase.GenerarResumenIaPacienteUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasDePacienteUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasDePacienteUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.nombreCompleto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FichaPacientePsicologoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPacientesDePsicologoUseCase: GetPacientesDePsicologoUseCase,
    private val observarNotasDePacienteUseCase: ObservarNotasDePacienteUseCase,
    private val observarTareasDePacienteUseCase: ObservarTareasDePacienteUseCase,
    private val sincronizarNotasDePacienteUseCase: SincronizarNotasDePacienteUseCase,
    private val sincronizarTareasDePacienteUseCase: SincronizarTareasDePacienteUseCase,
    private val generarResumenIaPacienteUseCase: GenerarResumenIaPacienteUseCase,
) : ViewModel() {

    private val pacienteId: Long = savedStateHandle.get<Long>(RutasApp.ARG_PACIENTE_ID) ?: 0L

    private val _uiState = MutableStateFlow(FichaPacientePsicologoUiState())
    val uiState: StateFlow<FichaPacientePsicologoUiState> = _uiState

    init {
        if (pacienteId != 0L) {
            viewModelScope.launch {
                observarNotasDePacienteUseCase(pacienteId)
                    .collectLatest { notas ->
                        _uiState.update { it.copy(notas = notas) }
                    }
            }
            viewModelScope.launch {
                observarTareasDePacienteUseCase(pacienteId)
                    .collectLatest { tareas ->
                        _uiState.update { it.copy(tareas = tareas) }
                    }
            }
        }
    }

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
            val nombrePaciente = pacienteEnLista?.let { listOf(it.nombre, it.apellidos).filter { p -> p.isNotBlank() }.joinToString(" ") }
                .orEmpty()
            val fotoPaciente = pacienteEnLista?.fotoPerfilUrl

            val resultadoNotas = sincronizarNotasDePacienteUseCase(pacienteId)
            val resultadoTareas = sincronizarTareasDePacienteUseCase(pacienteId)

            val errNotas = resultadoNotas.exceptionOrNull()
            val errTareas = resultadoTareas.exceptionOrNull()
            val mensajeError = when {
                errNotas != null && errTareas != null ->
                    listOfNotNull(errNotas.message, errTareas.message).joinToString(" · ")
                errNotas != null -> errNotas.message ?: "No se pudieron cargar las notas"
                errTareas != null -> errTareas.message ?: "No se pudieron cargar las tareas"
                else -> null
            }

            _uiState.update {
                it.copy(
                    cargando = false,
                    nombreUsuarioPaciente = nombrePaciente,
                    fotoPerfilUrlPaciente = fotoPaciente,
                    mensajeError = mensajeError,
                )
            }
        }
    }

    fun generarResumenIa() {
        if (pacienteId == 0L || _uiState.value.cargandoResumenIa) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    cargandoResumenIa = true,
                    errorResumenIa = null,
                )
            }

            generarResumenIaPacienteUseCase(pacienteId).fold(
                onSuccess = { resumen ->
                    _uiState.update {
                        it.copy(
                            cargandoResumenIa = false,
                            resumenIa = resumen.resumen,
                            numeroNotasAnalizadasIa = resumen.numeroNotasAnalizadas,
                            errorResumenIa = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargandoResumenIa = false,
                            errorResumenIa = error.message ?: "No se pudo generar el resumen",
                        )
                    }
                },
            )
        }
    }

    fun descartarResumenIa() {
        _uiState.update {
            it.copy(
                resumenIa = null,
                errorResumenIa = null,
                numeroNotasAnalizadasIa = 0,
            )
        }
    }
}
