package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObtenerDisponibilidadDiaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObservarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ReservarCitaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.presentation.ui.citas.calcularHistorialPreview
import dam2.tfg.psicologiaapp.presentation.ui.citas.calcularProximaCitaActiva
import dam2.tfg.psicologiaapp.presentation.ui.citas.generarDiasLaborablesProximos
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitasMenuViewModel @Inject constructor(
    private val observarMisCitasPacienteUseCase: ObservarMisCitasPacienteUseCase,
    private val sincronizarMisCitasPacienteUseCase: SincronizarMisCitasPacienteUseCase,
    private val obtenerDisponibilidadDiaUseCase: ObtenerDisponibilidadDiaUseCase,
    private val reservarCitaUseCase: ReservarCitaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitasMenuUiState())
    val uiState: StateFlow<CitasMenuUiState> = _uiState

    init {
        val dias = generarDiasLaborablesProximos()
        _uiState.update {
            it.copy(
                diasReservaRapida = dias,
                fechaReservaRapidaSeleccionada = dias.firstOrNull(),
                zonaHoraria = ZoneId.systemDefault().id,
            )
        }

        viewModelScope.launch {
            observarMisCitasPacienteUseCase().collectLatest { citas ->
                _uiState.update {
                    it.copy(
                        citas = citas,
                        proximaCita = calcularProximaCitaActiva(citas),
                        historialPreview = calcularHistorialPreview(citas),
                    )
                }
            }
        }
    }

    fun recargarCitas() {
        viewModelScope.launch {
            val hayDatos = _uiState.value.citas.isNotEmpty()
            if (!hayDatos) {
                _uiState.update { it.copy(cargandoCitas = true, mensajeError = null) }
            }
            sincronizarMisCitasPacienteUseCase().fold(
                onSuccess = { _uiState.update { it.copy(cargandoCitas = false, mensajeError = null) } },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargandoCitas = false,
                            mensajeError = error.message ?: "No se pudieron cargar las citas",
                        )
                    }
                },
            )
        }
    }

    fun seleccionarDiaReservaRapida(fecha: LocalDate) {
        _uiState.update {
            it.copy(
                fechaReservaRapidaSeleccionada = fecha,
                disponibilidad = null,
                mensajeError = null,
            )
        }
        cargarDisponibilidad()
    }

    fun cargarDisponibilidad() {
        val fecha = _uiState.value.fechaReservaRapidaSeleccionada ?: return
        if (fecha.dayOfWeek == DayOfWeek.SATURDAY || fecha.dayOfWeek == DayOfWeek.SUNDAY) {
            _uiState.update {
                it.copy(
                    cargandoDisponibilidad = false,
                    disponibilidad = null,
                    mensajeError = "No hay citas disponibles los fines de semana.",
                )
            }
            return
        }

        val zonaHoraria = _uiState.value.zonaHoraria.ifBlank { ZoneId.systemDefault().id }

        viewModelScope.launch {
            _uiState.update { it.copy(cargandoDisponibilidad = true) }
            obtenerDisponibilidadDiaUseCase(fecha, zonaHoraria).fold(
                onSuccess = { dispo ->
                    _uiState.update {
                        it.copy(
                            cargandoDisponibilidad = false,
                            disponibilidad = dispo,
                            mensajeError = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargandoDisponibilidad = false,
                            disponibilidad = null,
                            mensajeError = error.message ?: "No se pudo cargar la disponibilidad",
                        )
                    }
                },
            )
        }
    }

    fun reservar(fecha: LocalDate, hora: LocalTime) {
        val zona = ZoneId.of(_uiState.value.zonaHoraria.ifBlank { ZoneId.systemDefault().id })
        val inicioIsoOffset = ZonedDateTime.of(fecha, hora, zona).toOffsetDateTime().toString()

        viewModelScope.launch {
            _uiState.update { it.copy(cargandoReserva = true, mensajeError = null) }
            reservarCitaUseCase(inicioIsoOffset = inicioIsoOffset, zonaHoraria = zona.id).fold(
                onSuccess = {
                    _uiState.update { it.copy(cargandoReserva = false) }
                    recargarCitas()
                    cargarDisponibilidad()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargandoReserva = false,
                            mensajeError = error.message ?: "No se pudo reservar la cita",
                        )
                    }
                    cargarDisponibilidad()
                },
            )
        }
    }
}
