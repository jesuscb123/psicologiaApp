package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.nota.domain.usecase.BorrarNotaUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ListarPsicologosUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.AceptarTareaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.MarcarTareaRealizadaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePacienteViewModel @Inject constructor(
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val listarPsicologosUseCase: ListarPsicologosUseCase,
    private val observarNotasPacienteActualUseCase: ObservarNotasPacienteActualUseCase,
    private val observarTareasPacienteActualUseCase: ObservarTareasPacienteActualUseCase,
    private val sincronizarNotasPacienteActualUseCase: SincronizarNotasPacienteActualUseCase,
    private val sincronizarTareasPacienteActualUseCase: SincronizarTareasPacienteActualUseCase,
    private val aceptarTareaUseCase: AceptarTareaUseCase,
    private val marcarTareaRealizadaUseCase: MarcarTareaRealizadaUseCase,
    private val borrarNotaUseCase: BorrarNotaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePacienteUiState())
    val uiState: StateFlow<HomePacienteUiState> = _uiState

    private var trabajoRecarga: Job? = null

    init {
        viewModelScope.launch {
            observarNotasPacienteActualUseCase()
                .collectLatest { notas ->
                    _uiState.update { it.copy(notas = notas) }
                }
        }
        viewModelScope.launch {
            observarTareasPacienteActualUseCase()
                .collectLatest { tareas ->
                    _uiState.update { it.copy(tareas = tareas) }
                }
        }
    }

    fun recargar() {
        trabajoRecarga?.cancel()
        trabajoRecarga = viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoPerfil = getPerfilActualUseCase()
            ensureActive()
            val resultadoPsicologos = listarPsicologosUseCase()
            ensureActive()

            var perfilPaciente: PacientePerfil? = null
            resultadoPerfil.fold(
                onSuccess = { perfil ->
                    if (perfil.rol != RolUsuario.PACIENTE) {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = "Perfil no válido para paciente"
                            )
                        }
                        return@launch
                    }
                    perfilPaciente = perfil as? PacientePerfil
                    if (perfilPaciente == null) {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = "No se pudo cargar el perfil de paciente"
                            )
                        }
                        return@launch
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el perfil"
                        )
                    }
                    return@launch
                }
            )

            val listaPsicologos = resultadoPsicologos.getOrElse { emptyList() }
            val psicologoAsignado = obtenerPsicologoAsignado(
                listaPsicologos = listaPsicologos,
                psicologoId = perfilPaciente?.psicologoId
            )

            _uiState.update {
                it.copy(
                    perfilPaciente = perfilPaciente,
                    listaPsicologos = listaPsicologos,
                    psicologoAsignado = psicologoAsignado,
                    mensajeError = resultadoPsicologos.exceptionOrNull()?.message,
                )
            }

            if (perfilPaciente?.psicologoId != null) {
                val resultadoNotas = sincronizarNotasPacienteActualUseCase()
                ensureActive()
                val resultadoTareas = sincronizarTareasPacienteActualUseCase()
                ensureActive()
                val errNotas = resultadoNotas.exceptionOrNull()
                val errTareas = resultadoTareas.exceptionOrNull()
                val textoErrorCarga = when {
                    errNotas != null && errTareas != null ->
                        listOfNotNull(errNotas.message, errTareas.message).joinToString(" · ")
                    errNotas != null -> errNotas.message ?: "No se pudieron cargar las notas"
                    errTareas != null -> errTareas.message ?: "No se pudieron cargar las tareas"
                    else -> null
                }
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = textoErrorCarga ?: it.mensajeError,
                    )
                }
            } else {
                _uiState.update { it.copy(cargando = false, notas = emptyList(), tareas = emptyList()) }
            }
        }
    }

    fun aceptarTarea(tareaId: Long) {
        viewModelScope.launch {
            aceptarTareaUseCase(tareaId).fold(
                onSuccess = { _uiState.update { it.copy(mensajeError = null) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mensajeError = error.message ?: "No se pudo aceptar la tarea"
                        )
                    }
                }
            )
        }
    }

    fun marcarTareaRealizada(tareaId: Long, realizada: Boolean) {
        viewModelScope.launch {
            marcarTareaRealizadaUseCase(tareaId, realizada).fold(
                onSuccess = { _uiState.update { it.copy(mensajeError = null) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mensajeError = error.message
                                ?: "No se pudo actualizar el estado de la tarea"
                        )
                    }
                }
            )
        }
    }

    fun eliminarNota(notaId: Long) {
        viewModelScope.launch {
            borrarNotaUseCase(notaId).fold(
                onSuccess = { _uiState.update { it.copy(mensajeError = null) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mensajeError = error.message ?: "No se pudo eliminar la nota"
                        )
                    }
                }
            )
        }
    }

    private fun obtenerPsicologoAsignado(
        listaPsicologos: List<Psicologo>,
        psicologoId: Long?,
    ): Psicologo? {
        if (psicologoId == null) return null
        return listaPsicologos.firstOrNull { it.idEntidadPsicologo == psicologoId }
    }
}

