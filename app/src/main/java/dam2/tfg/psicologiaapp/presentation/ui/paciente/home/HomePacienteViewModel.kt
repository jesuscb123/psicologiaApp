package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import kotlinx.coroutines.CancellationException
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.ObservarNoLeidosEnChatUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.nota.domain.usecase.BorrarNotaUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.ObservarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.AceptarTareaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.MarcarTareaRealizadaUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.ObservarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
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
    private val observarNoLeidosEnChatUseCase: ObservarNoLeidosEnChatUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePacienteUiState())
    val uiState: StateFlow<HomePacienteUiState> = _uiState

    private var trabajoSincronizacion: Job? = null

    init {
        viewModelScope.launch {
            observarPerfilCacheadoUseCase().collectLatest { perfil ->
                if (perfil == null) return@collectLatest
                _uiState.update { it.copy(perfilPaciente = perfil.toPacientePerfil()) }
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
        viewModelScope.launch {
            try {
                observarPerfilCacheadoUseCase().collectLatest { perfil ->
                    val uid = perfil?.firebaseUid ?: return@collectLatest
                    try {
                        observarNoLeidosEnChatUseCase(uid).collectLatest { chatIds ->
                            _uiState.update { it.copy(tieneMensajeNoLeido = chatIds.isNotEmpty()) }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        _uiState.update { it.copy(tieneMensajeNoLeido = false) }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(tieneMensajeNoLeido = false) }
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

            val perfilPaciente = resolverPacientePerfilTrasSync(resultadoPerfil)
            ensureActive()

            if (!hayDatos && perfilPaciente == null) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = "No se pudo cargar el perfil")
                }
                return@launch
            }

            val psicologoId = perfilPaciente?.psicologoId

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
            } else if (perfilPaciente != null) {
                _uiState.update {
                    it.copy(cargando = false, notas = emptyList(), tareas = emptyList(), proximaCita = null)
                }
            } else {
                _uiState.update { it.copy(cargando = false) }
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

    private suspend fun resolverPacientePerfilTrasSync(resultadoPerfil: Result<Unit>): PacientePerfil? {
        if (resultadoPerfil.isSuccess) {
            val perfilApi = getPerfilActualUseCase().getOrNull() as? PacientePerfil
            if (perfilApi != null) {
                _uiState.update { it.copy(perfilPaciente = perfilApi) }
                return perfilApi
            }
        }
        _uiState.value.perfilPaciente?.let { return it }
        val perfilFallback = getPerfilActualUseCase().getOrNull() as? PacientePerfil
        if (perfilFallback != null) {
            _uiState.update { it.copy(perfilPaciente = perfilFallback) }
        }
        return perfilFallback
    }

    private fun PerfilCacheado.toPacientePerfil(): PacientePerfil = PacientePerfil(
        usuarioId = usuarioId,
        firebaseUid = firebaseUid,
        nombre = nombre,
        apellidos = apellidos,
        email = "",
        fotoPerfilUrl = fotoPerfilUrl,
        psicologoId = psicologoId,
    )

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
