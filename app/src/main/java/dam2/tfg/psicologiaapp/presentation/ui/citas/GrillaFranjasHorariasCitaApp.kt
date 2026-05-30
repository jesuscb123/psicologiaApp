package dam2.tfg.psicologiaapp.presentation.ui.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GrillaFranjasHorariasCitaApp(
    fecha: LocalDate,
    zonaHoraria: String,
    horasDisponibles: List<LocalTime>?,
    cargando: Boolean,
    alSeleccionarHora: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    horaSeleccionada: LocalTime? = null,
    mensajeDisponibilidadNula: String = "Selecciona o cambia la fecha para ver la disponibilidad.",
) {
    val zona = runCatching { ZoneId.of(zonaHoraria.ifBlank { ZoneId.systemDefault().id }) }
        .getOrElse { ZoneId.systemDefault() }
    val ahora = ZonedDateTime.now(zona)
    val esHoy = fecha == ahora.toLocalDate()
    val fueraDeHorarioHoy = esHoy && ahora.toLocalTime() >= LocalTime.of(17, 0)
    val slotsDia = remember { (9..16).map { LocalTime.of(it, 0) } }

    when {
        cargando -> {
            Text(
                text = "Cargando...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier,
            )
        }
        horasDisponibles == null -> {
            Text(
                text = mensajeDisponibilidadNula,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier,
            )
        }
        fueraDeHorarioHoy -> {
            Text(
                text = "No hay horarios disponibles para esta fecha.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier,
            )
        }
        else -> {
            val horasDisponiblesSet = horasDisponibles.toSet()
            val todasDeshabilitadas = slotsDia.all { slot ->
                val pasada = esHoy && slot.isBefore(ahora.toLocalTime())
                val disponibleServidor = slot in horasDisponiblesSet
                !disponibleServidor || pasada
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.fillMaxWidth(),
            ) {
                if (todasDeshabilitadas) {
                    Text(
                        text = "No hay horas disponibles para este día.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                slotsDia.forEach { hora ->
                    val pasada = esHoy && hora.isBefore(ahora.toLocalTime())
                    val disponibleServidor = hora in horasDisponiblesSet
                    val habilitada = disponibleServidor && !pasada && !cargando

                    FilterChip(
                        selected = horaSeleccionada == hora,
                        onClick = {
                            if (habilitada) alSeleccionarHora(hora)
                        },
                        label = { Text(hora.toString()) },
                        enabled = habilitada,
                    )
                }
            }
        }
    }
}
