package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObtenerDisponibilidadDiaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ReservarCitaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitasViewModel @Inject constructor(
    private val obtenerDisponibilidadDiaUseCase: ObtenerDisponibilidadDiaUseCase,
    private val reservarCitaUseCase: ReservarCitaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CitasUiState(zonaHoraria = ZoneId.systemDefault().id)
    )
    val uiState: StateFlow<CitasUiState> = _uiState

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }

    fun seleccionarFecha(fecha: LocalDate) {
        _uiState.update {
            it.copy(
                fechaSeleccionada = fecha,
                horaSeleccionada = null,
                mensajeError = null,
            )
        }
        cargarDisponibilidad()
    }

    fun seleccionarHora(hora: LocalTime) {
        _uiState.update { it.copy(horaSeleccionada = hora, mensajeError = null) }
    }

    fun cargarDisponibilidad() {
        val fecha = _uiState.value.fechaSeleccionada
        if (fecha.dayOfWeek == DayOfWeek.SATURDAY || fecha.dayOfWeek == DayOfWeek.SUNDAY) {
            _uiState.update {
                it.copy(
                    cargando = false,
                    disponibilidad = null,
                    mensajeError = "No hay citas disponibles los fines de semana.",
                )
            }
            return
        }

        val zonaHoraria = _uiState.value.zonaHoraria.ifBlank { ZoneId.systemDefault().id }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            obtenerDisponibilidadDiaUseCase(fecha, zonaHoraria).fold(
                onSuccess = { dispo ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            disponibilidad = dispo,
                            mensajeError = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            disponibilidad = null,
                            mensajeError = error.message ?: "No se pudo cargar la disponibilidad",
                        )
                    }
                },
            )
        }
    }

    fun reservar() {
        val estado = _uiState.value
        val hora = estado.horaSeleccionada
        if (hora == null) {
            _uiState.update { it.copy(mensajeError = "Selecciona una hora") }
            return
        }

        val zona = ZoneId.of(estado.zonaHoraria.ifBlank { ZoneId.systemDefault().id })
        val inicioIsoOffset = ZonedDateTime.of(
            estado.fechaSeleccionada,
            hora,
            zona,
        ).toOffsetDateTime().toString()

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            reservarCitaUseCase(inicioIsoOffset = inicioIsoOffset, zonaHoraria = zona.id).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            horaSeleccionada = null,
                            eventoNavegacion = EventoNavegacionCitas.CitaReservada,
                        )
                    }
                    // Recargar disponibilidad para invalidar el slot reservado
                    cargarDisponibilidad()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            horaSeleccionada = null,
                            mensajeError = error.message ?: "No se pudo reservar la cita",
                        )
                    }
                    cargarDisponibilidad()
                },
            )
        }
    }
}

