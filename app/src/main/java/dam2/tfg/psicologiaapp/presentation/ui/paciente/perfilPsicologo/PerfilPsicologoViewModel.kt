package dam2.tfg.psicologiaapp.presentation.ui.paciente.perfilPsicologo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.paciente.domain.usecase.AsignarPsicologoUseCase
import dam2.tfg.psicologiaapp.paciente.domain.usecase.CancelarTerapiaUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilPsicologoViewModel @Inject constructor(
    private val observarPsicologosUseCase: ObservarPsicologosUseCase,
    private val sincronizarPsicologosUseCase: SincronizarPsicologosUseCase,
    private val asignarPsicologoUseCase: AsignarPsicologoUseCase,
    private val cancelarTerapiaUseCase: CancelarTerapiaUseCase,
    private val observarPerfilCacheadoUseCase: ObservarPerfilCacheadoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilPsicologoUiState())
    val uiState: StateFlow<PerfilPsicologoUiState> = _uiState

    private var psicologoIdActual: Long? = null

    init {
        viewModelScope.launch {
            observarPsicologosUseCase().collectLatest { lista ->
                val id = psicologoIdActual ?: return@collectLatest
                val psicologo = lista.firstOrNull { it.usuarioId == id || it.idEntidadPsicologo == id }
                if (psicologo != null) {
                    _uiState.update { it.copy(cargando = false, psicologo = psicologo, mensajeError = null) }
                }
            }
        }
        viewModelScope.launch {
            observarPerfilCacheadoUseCase().collectLatest { perfil ->
                _uiState.update {
                    it.copy(
                        pacienteYaTienePsicologo = perfil?.psicologoId != null,
                        psicologoAsignadoId = perfil?.psicologoId,
                    )
                }
            }
        }
    }

    fun cargar(psicologoId: String) {
        val idLong = psicologoId.toLongOrNull()
        if (idLong == null) {
            _uiState.update { it.copy(mensajeError = "Id de psicólogo inválido") }
            return
        }
        psicologoIdActual = idLong

        viewModelScope.launch {
            val hayDatos = _uiState.value.psicologo != null
            if (!hayDatos) {
                _uiState.update { it.copy(cargando = true, mensajeError = null) }
            }
            // Sync in background; Room Flow will update via init collector.
            sincronizarPsicologosUseCase().onFailure { error ->
                if (!hayDatos) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el psicólogo"
                        )
                    }
                }
            }
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

    fun cancelarTerapia() {
        viewModelScope.launch {
            _uiState.update { it.copy(cancelandoTerapia = true, mensajeError = null) }
            val resultado = cancelarTerapiaUseCase()
            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cancelandoTerapia = false,
                            eventoNavegacion = EventoNavegacionPerfilPsicologo.CancelacionTerapiaCompletada,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cancelandoTerapia = false,
                            mensajeError = error.message ?: "No se pudo cancelar la terapia",
                        )
                    }
                },
            )
        }
    }
}
