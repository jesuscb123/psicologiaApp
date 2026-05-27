package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import dam2.tfg.psicologiaapp.ui.theme.colorFondoTarjetaAzulActivaApp
import dam2.tfg.psicologiaapp.ui.theme.colorFondoTarjetaAzulSuaveApp

@Composable
fun TarjetaCitaPacienteApp(
    cita: Cita,
    mostrarCancelar: Boolean,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TarjetaCitaApp(
        etiquetaPersona = "Psicólogo",
        nombrePersona = cita.nombrePsicologo,
        inicioIso = cita.inicio,
        finIso = cita.fin,
        estado = cita.estadoCalculado,
        modifier = modifier,
        accionInferior = if (mostrarCancelar) {
            {
                OutlinedButton(
                    onClick = alCancelar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    ),
                ) {
                    Text(
                        text = "Cancelar cita",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        } else {
            null
        },
    )
}

@Composable
fun TarjetaCitaPsicologoApp(
    cita: Cita,
    modifier: Modifier = Modifier,
) {
    TarjetaCitaApp(
        etiquetaPersona = "Paciente",
        nombrePersona = cita.nombrePaciente,
        inicioIso = cita.inicio,
        finIso = cita.fin,
        estado = cita.estadoCalculado,
        modifier = modifier,
    )
}

@Composable
fun TarjetaCitaApp(
    etiquetaPersona: String,
    nombrePersona: String,
    inicioIso: String,
    finIso: String?,
    estado: EstadoCitaCalculado,
    modifier: Modifier = Modifier,
    accionInferior: (@Composable () -> Unit)? = null,
) {
    val esActiva = estado == EstadoCitaCalculado.ACTIVA
    val fondoTarjeta = if (esActiva) {
        colorFondoTarjetaAzulActivaApp()
    } else {
        colorFondoTarjetaAzulSuaveApp()
    }
    val bordeTarjeta = MaterialTheme.colorScheme.primary.copy(
        alpha = if (esActiva) 0.22f else 0.12f,
    )
    val inicioFormateado = remember(inicioIso) { formatearFechaHoraCita(inicioIso) }
    val finFormateado = remember(finIso) { finIso?.let { formatearFechaHoraCita(it) } }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(width = 1.dp, color = bordeTarjeta),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (esActiva) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    },
                ) {
                    Text(
                        text = if (esActiva) "Activa" else "Finalizada",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (esActiva) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = etiquetaPersona.uppercase(Locale("es")),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                )
                Text(
                    text = nombrePersona,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaDetalleCita(
                    icono = Icons.Outlined.DateRange,
                    etiqueta = "Inicio",
                    valor = inicioFormateado,
                    destacado = esActiva,
                )
                if (finFormateado != null) {
                    FilaDetalleCita(
                        icono = Icons.Outlined.DateRange,
                        etiqueta = "Fin",
                        valor = finFormateado,
                        destacado = esActiva,
                    )
                }
            }

            accionInferior?.invoke()
        }
    }
}

@Composable
private fun FilaDetalleCita(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    destacado: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = if (destacado) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            },
            modifier = Modifier.size(18.dp),
        )
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (destacado) FontWeight.Medium else FontWeight.Normal,
                color = if (destacado) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

private fun formatearFechaHoraCita(isoOffset: String): String =
    runCatching {
        val instant = OffsetDateTime.parse(isoOffset).toInstant()
        instant.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"))
    }.getOrElse { isoOffset }
