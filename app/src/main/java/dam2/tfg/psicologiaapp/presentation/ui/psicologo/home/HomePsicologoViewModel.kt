package dam2.tfg.psicologiaapp.presentation.ui.psicologo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam2.tfg.psicologiaapp.chat.domain.usecase.ObservarNoLeidosEnChatUseCase
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.ObservarAlertasRiesgoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class HomePsicologoViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val observarPacientesDePsicologoUseCase: ObservarPacientesDePsicologoUseCase,
    private val sincronizarPacientesDePsicologoUseCase: SincronizarPacientesDePsicologoUseCase,
    private val observarMisCitasPsicologoUseCase: ObservarMisCitasPsicologoUseCase,
    private val sincronizarMisCitasPsicologoUseCase: SincronizarMisCitasPsicologoUseCase,
    private val sincronizarPerfilActualUseCase: SincronizarPerfilActualUseCase,
    private val observarNoLeidosEnChatUseCase: ObservarNoLeidosEnChatUseCase,
    private val observarAlertasRiesgoUseCase: ObservarAlertasRiesgoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePsicologoUiState())
    val uiState: StateFlow<HomePsicologoUiState> = _uiState

    private var trabajoSincronizacion: Job? = null

    init {
        viewModelScope.launch {
            observarPacientesDePsicologoUseCase().collectLatest { lista ->
                _uiState.update { it.copy(listaPacientes = lista) }
            }
        }
        viewModelScope.launch {
            observarMisCitasPsicologoUseCase().collectLatest { citas ->
                val ahora = OffsetDateTime.now()
                val mapa = citas
                    .filter { it.estadoCalculado == EstadoCitaCalculado.ACTIVA }
                    .filter { OffsetDateTime.parse(it.inicio) >= ahora }
                    .groupBy { it.pacienteId }
                    .mapValues { (_, citasPaciente) ->
                        citasPaciente.minByOrNull { OffsetDateTime.parse(it.inicio) }
                    }
                _uiState.update { it.copy(mapaCitaProxima = mapa) }
            }
        }
        val psicologoUid = firebaseAuth.currentUser?.uid.orEmpty()
        viewModelScope.launch {
            try {
                observarNoLeidosEnChatUseCase(psicologoUid).collectLatest { chatIds ->
                    val mapa = chatIds
                        .mapNotNull { chatId -> parsearPacienteIdDeChatId(chatId)?.let { it to true } }
                        .toMap()
                    _uiState.update { it.copy(mapaNoLeidosPorPaciente = mapa) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(mapaNoLeidosPorPaciente = emptyMap()) }
            }
        }
        viewModelScope.launch {
            try {
                observarAlertasRiesgoUseCase(psicologoUid).collectLatest { pacienteIds ->
                    val mapa = pacienteIds.associateWith { true }
                    _uiState.update { it.copy(mapaRiesgoPorPaciente = mapa) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(mapaRiesgoPorPaciente = emptyMap()) }
            }
        }
    }

    private fun parsearPacienteIdDeChatId(chatId: String): Long? {
        val regex = Regex("^paciente_(\\d+)_psicologo_(\\d+)$")
        return regex.matchEntire(chatId)?.groupValues?.get(1)?.toLongOrNull()
    }

    /**
     * Sincroniza datos con el servidor. Solo activa el indicador de carga
     * la primera vez (cuando Room no tiene datos aún).
     */
    fun sincronizarSiProcede() {
        trabajoSincronizacion?.cancel()
        trabajoSincronizacion = viewModelScope.launch {
            val hayDatos = _uiState.value.listaPacientes.isNotEmpty()
            if (!hayDatos) {
                _uiState.update { it.copy(cargando = true, mensajeError = null) }
            }

            sincronizarPerfilActualUseCase()
            ensureActive()

            val resultadoPacientes = sincronizarPacientesDePsicologoUseCase()
            ensureActive()

            sincronizarMisCitasPsicologoUseCase()

            _uiState.update {
                it.copy(
                    cargando = false,
                    mensajeError = resultadoPacientes.exceptionOrNull()?.message,
                )
            }
        }
    }

    fun recargar() = sincronizarSiProcede()
}
