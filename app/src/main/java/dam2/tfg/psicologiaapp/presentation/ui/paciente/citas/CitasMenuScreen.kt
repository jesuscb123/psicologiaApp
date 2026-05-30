package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.BotonSecundarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.ui.citas.DialogoConfirmarCitaApp
import dam2.tfg.psicologiaapp.presentation.ui.citas.FilaHistorialSesionApp
import dam2.tfg.psicologiaapp.presentation.ui.citas.GrillaFranjasHorariasCitaApp
import dam2.tfg.psicologiaapp.presentation.ui.citas.formatearInicioCitaResumen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CitasMenuScreen(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    alIrAgendar: () -> Unit,
    alIrMisCitas: () -> Unit,
    viewModel: CitasMenuViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var horaParaConfirmar by remember { mutableStateOf<LocalTime?>(null) }
    val fechaSeleccionada = uiState.fechaReservaRapidaSeleccionada

    LaunchedEffect(Unit) {
        viewModel.recargarCitas()
        viewModel.cargarDisponibilidad()
    }

    horaParaConfirmar?.let { hora ->
        fechaSeleccionada?.let { fecha ->
            DialogoConfirmarCitaApp(
                fecha = fecha,
                hora = hora,
                alConfirmar = {
                    viewModel.reservar(fecha, hora)
                    horaParaConfirmar = null
                },
                alCancelar = { horaParaConfirmar = null },
            )
        }
    }

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Mis Sesiones",
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            uiState.mensajeError?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                TarjetaProximaCita(
                    proximaCita = uiState.proximaCita,
                    cargando = uiState.cargandoCitas,
                    alReservarNuevaSesion = alIrAgendar,
                )
            }

            if (uiState.diasReservaRapida.isNotEmpty()) {
                item {
                    TarjetaReservaRapida(
                        dias = uiState.diasReservaRapida,
                        fechaSeleccionada = fechaSeleccionada,
                        zonaHoraria = uiState.zonaHoraria,
                        horasDisponibles = uiState.disponibilidad?.horasDisponibles,
                        cargandoDisponibilidad = uiState.cargandoDisponibilidad || uiState.cargandoReserva,
                        alSeleccionarDia = viewModel::seleccionarDiaReservaRapida,
                        alSeleccionarHora = { horaParaConfirmar = it },
                    )
                }
            }

            item {
                TarjetaHistorialSesiones(
                    historial = uiState.historialPreview,
                    alVerHistorialCompleto = alIrMisCitas,
                )
            }
        }
    }
}

@Composable
private fun TarjetaHubCitas(
    titulo: String,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            contenido()
        }
    }
}

@Composable
private fun TarjetaProximaCita(
    proximaCita: dam2.tfg.psicologiaapp.cita.domain.model.Cita?,
    cargando: Boolean,
    alReservarNuevaSesion: () -> Unit,
) {
    TarjetaHubCitas(titulo = "Tu Próxima Cita") {
        if (proximaCita != null) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = proximaCita.nombrePsicologo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = formatearInicioCitaResumen(proximaCita.inicio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = if (cargando) {
                        "Consultando próxima cita…"
                    } else {
                        "No tienes citas activas. Tu bienestar es lo primero."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        BotonPrimarioApp(
            texto = "Reservar Nueva Sesión",
            alPulsar = alReservarNuevaSesion,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TarjetaReservaRapida(
    dias: List<LocalDate>,
    fechaSeleccionada: LocalDate?,
    zonaHoraria: String,
    horasDisponibles: List<LocalTime>?,
    cargandoDisponibilidad: Boolean,
    alSeleccionarDia: (LocalDate) -> Unit,
    alSeleccionarHora: (LocalTime) -> Unit,
) {
    val formatoDiaSemana = remember { DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-ES")) }
    val formatoDia = remember { DateTimeFormatter.ofPattern("d", Locale.getDefault()) }
    val formatoMes = remember { DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("es-ES")) }

    TarjetaHubCitas(titulo = "Reserva Rápida") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(dias, key = { it.toString() }) { dia ->
                val seleccionado = dia == fechaSeleccionada
                FilterChip(
                    selected = seleccionado,
                    onClick = { alSeleccionarDia(dia) },
                    label = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 4.dp),
                        ) {
                            Text(
                                text = dia.format(formatoDiaSemana).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = dia.format(formatoDia),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = dia.format(formatoMes).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
            }
        }

        Text(
            text = "Horario de atención habitual: 09:00–17:00",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        fechaSeleccionada?.let { fecha ->
            GrillaFranjasHorariasCitaApp(
                fecha = fecha,
                zonaHoraria = zonaHoraria,
                horasDisponibles = horasDisponibles,
                cargando = cargandoDisponibilidad,
                alSeleccionarHora = alSeleccionarHora,
            )
        }
    }
}

@Composable
private fun TarjetaHistorialSesiones(
    historial: List<dam2.tfg.psicologiaapp.cita.domain.model.Cita>,
    alVerHistorialCompleto: () -> Unit,
) {
    TarjetaHubCitas(titulo = "Historial de Sesiones") {
        if (historial.isEmpty()) {
            Text(
                text = "Aún no tienes sesiones finalizadas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                historial.forEach { cita ->
                    FilaHistorialSesionApp(cita = cita)
                }
            }
        }

        BotonSecundarioApp(
            texto = "Ver historial completo",
            alPulsar = alVerHistorialCompleto,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
