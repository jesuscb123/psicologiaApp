package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPacientesDePsicologoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val observarPacientesDePsicologoUseCase: ObservarPacientesDePsicologoUseCase,
    private val sincronizarPacientesDePsicologoUseCase: SincronizarPacientesDePsicologoUseCase,
    private val observarMisCitasPsicologoUseCase: ObservarMisCitasPsicologoUseCase,
    private val sincronizarMisCitasPsicologoUseCase: SincronizarMisCitasPsicologoUseCase,
    private val sincronizarPerfilActualUseCase: SincronizarPerfilActualUseCase,
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
