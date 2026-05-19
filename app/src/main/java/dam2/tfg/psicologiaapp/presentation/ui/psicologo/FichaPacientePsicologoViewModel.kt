package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasDePacienteUseCase
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.resumenIa.domain.usecase.GenerarResumenIaPacienteUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasDePacienteUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasDePacienteUseCase
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
    private val sincronizarPacientesDePsicologoUseCase: SincronizarPacientesDePsicologoUseCase,
    private val pacienteDao: PacienteDao,
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
            // Observe paciente from Room cache (name/photo available instantly on re-entry).
            viewModelScope.launch {
                pacienteDao.observarPorId(pacienteId).collectLatest { entity ->
                    if (entity != null) {
                        val nombre = listOf(entity.nombre, entity.apellidos)
                            .filter { it.isNotBlank() }.joinToString(" ")
                        if (nombre.isNotBlank()) {
                            _uiState.update {
                                it.copy(
                                    nombreUsuarioPaciente = nombre,
                                    fotoPerfilUrlPaciente = entity.fotoPerfilUrl,
                                )
                            }
                        }
                    }
                }
            }
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
            val hayDatos = _uiState.value.nombreUsuarioPaciente.isNotEmpty()
            if (!hayDatos) {
                _uiState.update { it.copy(cargando = true, mensajeError = null) }
            }

            // Sync en background; Room Flow en init actualiza nombre/foto automáticamente
            val resultadoPacientes = sincronizarPacientesDePsicologoUseCase()
            val resultadoNotas = sincronizarNotasDePacienteUseCase(pacienteId)
            val resultadoTareas = sincronizarTareasDePacienteUseCase(pacienteId)

            val errores = listOfNotNull(
                resultadoPacientes.exceptionOrNull()?.message,
                resultadoNotas.exceptionOrNull()?.message,
                resultadoTareas.exceptionOrNull()?.message,
            )

            _uiState.update {
                it.copy(
                    cargando = false,
                    mensajeError = errores.firstOrNull(),
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
