package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CitasScreen(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: CitasViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDisponibilidad()
    }

    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var horaParaConfirmar by remember { mutableStateOf<LocalTime?>(null) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionCitas.CitaReservada -> {
                viewModel.alConsumirEventoNavegacion()
            }
            null -> Unit
        }
    }

    horaParaConfirmar?.let { hora ->
        AlertDialog(
            onDismissRequest = { horaParaConfirmar = null },
            title = { Text("Confirmar cita") },
            text = {
                Text(
                    "¿Deseas reservar una cita el ${
                        uiState.fechaSeleccionada.format(
                            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))
                        )
                    } a las ${hora.format(DateTimeFormatter.ofPattern("HH:mm"))}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.seleccionarHora(hora)
                        viewModel.reservar()
                        horaParaConfirmar = null
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { horaParaConfirmar = null }) { Text("Cancelar") }
            },
        )
    }

    if (mostrarSelectorFecha) {
        DatePickerDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val fecha = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.seleccionarFecha(fecha)
                        }
                        mostrarSelectorFecha = false
                    },
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorFecha = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Agendar cita",
                mostrarFlechaAtras = true,
                alVolver = alVolver,
                nombreUsuario = nombreUsuarioBarra,
                fotoPerfilUrl = fotoPerfilUrlBarra,
                revisionCacheFoto = revisionCacheFotoBarra,
                alAbrirMenuPerfil = alAbrirMenuPerfil,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Agendar cita",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            uiState.mensajeError?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Seleccionar fecha",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            IconButton(
                                onClick = { viewModel.seleccionarFecha(uiState.fechaSeleccionada.minusDays(1)) },
                                enabled = !uiState.cargando,
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Día anterior")
                            }
                            Text(
                                text = YearMonth.from(uiState.fechaSeleccionada)
                                    .format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("es-ES"))),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            IconButton(
                                onClick = { viewModel.seleccionarFecha(uiState.fechaSeleccionada.plusDays(1)) },
                                enabled = !uiState.cargando,
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Día siguiente")
                            }
                        }
                    }

                    Surface(
                        onClick = { mostrarSelectorFecha = true },
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = uiState.fechaSeleccionada.format(
                                    DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.forLanguageTag("es-ES")),
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    TextButton(
                        onClick = { viewModel.cargarDisponibilidad() },
                        enabled = !uiState.cargando,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(if (uiState.cargando) "Actualizando..." else "Refrescar disponibilidad")
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Franjas horarias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Horario de atención habitual: 09:00–17:00",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    val zona = runCatching { ZoneId.of(uiState.zonaHoraria.ifBlank { ZoneId.systemDefault().id }) }
                        .getOrElse { ZoneId.systemDefault() }
                    val ahora = ZonedDateTime.now(zona)
                    val esHoy = uiState.fechaSeleccionada == ahora.toLocalDate()
                    val fueraDeHorarioHoy = esHoy && ahora.toLocalTime() >= LocalTime.of(17, 0)
                    val slotsDia = remember {
                        (9..16).map { LocalTime.of(it, 0) }
                    }

                    when {
                        uiState.cargando -> {
                            Text("Cargando...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        uiState.disponibilidad == null -> {
                            Text(
                                "Selecciona o cambia la fecha para ver la disponibilidad.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        fueraDeHorarioHoy -> {
                            Text(
                                "No hay horarios disponibles para esta fecha.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        else -> {
                            val horasDisponiblesSet = uiState.disponibilidad?.horasDisponibles.orEmpty().toSet()
                            val todasDeshabilitadas = slotsDia.all { slot ->
                                val pasada = esHoy && slot.isBefore(ahora.toLocalTime())
                                val disponibleServidor = slot in horasDisponiblesSet
                                !disponibleServidor || pasada
                            }

                            if (todasDeshabilitadas) {
                                Text(
                                    "No hay horas disponibles para este día.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                slotsDia.forEach { hora ->
                                    val pasada = esHoy && hora.isBefore(ahora.toLocalTime())
                                    val disponibleServidor = hora in horasDisponiblesSet
                                    val habilitada = disponibleServidor && !pasada && !uiState.cargando

                                    FilterChip(
                                        selected = uiState.horaSeleccionada == hora,
                                        onClick = {
                                            if (habilitada) horaParaConfirmar = hora
                                        },
                                        label = { Text(hora.toString()) },
                                        enabled = habilitada,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding().height(4.dp))
        }
    }
}
