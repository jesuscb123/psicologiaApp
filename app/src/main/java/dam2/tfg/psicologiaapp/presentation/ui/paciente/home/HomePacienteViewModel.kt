package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.BorrarNotaUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.AceptarTareaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.MarcarTareaRealizadaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class HomePacienteViewModel @Inject constructor(
    private val observarPerfilCacheadoUseCase: ObservarPerfilCacheadoUseCase,
    private val sincronizarPerfilActualUseCase: SincronizarPerfilActualUseCase,
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val observarPsicologosUseCase: ObservarPsicologosUseCase,
    private val sincronizarPsicologosUseCase: SincronizarPsicologosUseCase,
    private val observarNotasPacienteActualUseCase: ObservarNotasPacienteActualUseCase,
    private val observarTareasPacienteActualUseCase: ObservarTareasPacienteActualUseCase,
    private val sincronizarNotasPacienteActualUseCase: SincronizarNotasPacienteActualUseCase,
    private val sincronizarTareasPacienteActualUseCase: SincronizarTareasPacienteActualUseCase,
    private val observarMisCitasPacienteUseCase: ObservarMisCitasPacienteUseCase,
    private val sincronizarMisCitasPacienteUseCase: SincronizarMisCitasPacienteUseCase,
    private val aceptarTareaUseCase: AceptarTareaUseCase,
    private val marcarTareaRealizadaUseCase: MarcarTareaRealizadaUseCase,
    private val borrarNotaUseCase: BorrarNotaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePacienteUiState())
    val uiState: StateFlow<HomePacienteUiState> = _uiState

    private var trabajoSincronizacion: Job? = null

    init {
        viewModelScope.launch {
            observarPerfilCacheadoUseCase().collectLatest { perfil ->
                if (perfil == null) return@collectLatest
                val pacientePerfil = dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil(
                    usuarioId = perfil.usuarioId,
                    firebaseUid = perfil.firebaseUid,
                    nombre = perfil.nombre,
                    apellidos = perfil.apellidos,
                    email = "",
                    fotoPerfilUrl = perfil.fotoPerfilUrl,
                    psicologoId = perfil.psicologoId,
                )
                _uiState.update { it.copy(perfilPaciente = pacientePerfil) }
            }
        }
        viewModelScope.launch {
            observarPsicologosUseCase().collectLatest { lista ->
                val psicologoAsignado = obtenerPsicologoAsignado(lista, _uiState.value.perfilPaciente?.psicologoId)
                _uiState.update { it.copy(listaPsicologos = lista, psicologoAsignado = psicologoAsignado) }
            }
        }
        viewModelScope.launch {
            observarNotasPacienteActualUseCase().collectLatest { notas ->
                _uiState.update { it.copy(notas = notas) }
            }
        }
        viewModelScope.launch {
            observarTareasPacienteActualUseCase().collectLatest { tareas ->
                _uiState.update { it.copy(tareas = tareas) }
            }
        }
        viewModelScope.launch {
            observarMisCitasPacienteUseCase().collectLatest { citas ->
                val proxima = calcularProximaCitaActiva(citas)
                _uiState.update { it.copy(proximaCita = proxima, cargandoProximaCita = false) }
            }
        }
    }

    /**
     * Sincroniza datos con el servidor. Solo muestra el indicador de carga
     * si no hay datos en caché local todavía (primera carga tras login).
     */
    fun sincronizarSiProcede() {
        trabajoSincronizacion?.cancel()
        trabajoSincronizacion = viewModelScope.launch {
            val hayDatos = _uiState.value.perfilPaciente != null
            if (!hayDatos) {
                _uiState.update { it.copy(cargando = true, mensajeError = null) }
            }

            // Sincronizar perfil: esto guarda en Room y dispara el Flow en init.
            val resultadoPerfil = sincronizarPerfilActualUseCase()
            ensureActive()

            if (!hayDatos && resultadoPerfil.isFailure) {
                // Como alternativa, intentar con getPerfilActualUseCase (sin guardar en caché)
                val perfilDirecto = getPerfilActualUseCase().getOrNull()
                if (perfilDirecto == null) {
                    _uiState.update {
                        it.copy(cargando = false, mensajeError = "No se pudo cargar el perfil")
                    }
                    return@launch
                }
                // Actualizar estado directamente si Room Flow no ha emitido todavía
                val perfil = perfilDirecto as? dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
                if (perfil != null && _uiState.value.perfilPaciente == null) {
                    _uiState.update { it.copy(perfilPaciente = perfil) }
                }
            }
            ensureActive()

            // PsicologoId puede venir de la caché o del perfil recién obtenido
            val psicologoId = _uiState.value.perfilPaciente?.psicologoId

            // Sync psicólogos siempre (para tener lista actualizada en Room)
            val errPsicologos = sincronizarPsicologosUseCase().exceptionOrNull()
            ensureActive()
            if (errPsicologos != null && _uiState.value.listaPsicologos.isEmpty()) {
                _uiState.update { it.copy(mensajeError = "No se pudo cargar la lista de profesionales") }
            }

            if (psicologoId != null) {
                sincronizarNotasPacienteActualUseCase()
                ensureActive()
                sincronizarTareasPacienteActualUseCase()
                ensureActive()
                val errCitas = sincronizarMisCitasPacienteUseCase().exceptionOrNull()
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = errCitas?.message,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(cargando = false, notas = emptyList(), tareas = emptyList(), proximaCita = null)
                }
            }
        }
    }

    fun aceptarTarea(tareaId: Long) {
        viewModelScope.launch {
            aceptarTareaUseCase(tareaId).fold(
                onSuccess = { _uiState.update { it.copy(mensajeError = null) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(mensajeError = error.message ?: "No se pudo aceptar la tarea")
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
                        it.copy(mensajeError = error.message ?: "No se pudo eliminar la nota")
                    }
                }
            )
        }
    }

    private fun obtenerPsicologoAsignado(
        listaPsicologos: List<dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo>,
        psicologoId: Long?,
    ): dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo? {
        if (psicologoId == null) return null
        return listaPsicologos.firstOrNull { it.idEntidadPsicologo == psicologoId }
    }

    private fun calcularProximaCitaActiva(citas: List<Cita>): Cita? {
        val ahora = Instant.now()
        return citas
            .asSequence()
            .filter { it.estadoCalculado == EstadoCitaCalculado.ACTIVA }
            .mapNotNull { cita ->
                val inicio = runCatching { OffsetDateTime.parse(cita.inicio).toInstant() }
                    .getOrNull() ?: return@mapNotNull null
                if (inicio.isBefore(ahora)) return@mapNotNull null
                cita to inicio
            }
            .minByOrNull { it.second }
            ?.first
    }
}

private fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> =
    fold(onSuccess = { Result.success(transform(it)) }, onFailure = { Result.failure(it) })
