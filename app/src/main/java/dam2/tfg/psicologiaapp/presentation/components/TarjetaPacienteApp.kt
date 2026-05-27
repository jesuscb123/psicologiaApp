package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TarjetaPacienteApp(
    paciente: Paciente,
    alPulsar: (Paciente) -> Unit,
    modifier: Modifier = Modifier,
    citaProxima: Cita? = null,
    alPulsarChat: ((Paciente) -> Unit)? = null,
    tieneNoLeidos: Boolean = false,
    tieneAlertaRiesgo: Boolean = false,
) {
    val nombreCompleto = listOf(paciente.nombre, paciente.apellidos)
        .filter { it.isNotBlank() }
        .joinToString(" ")

    val textoCita = remember(citaProxima) {
        if (citaProxima == null) {
            null
        } else {
            try {
                val formatter = DateTimeFormatter.ofPattern("EEE, d MMM · HH:mm", Locale("es"))
                val zoned = OffsetDateTime.parse(citaProxima.inicio)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                zoned.format(formatter)
                    .replaceFirstChar { it.titlecase(Locale("es")) }
            } catch (_: Exception) {
                null
            }
        }
    }

    val cardModifier = modifier.fillMaxWidth().let { base ->
        if (alPulsarChat == null) base.clickable { alPulsar(paciente) } else base
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        ),
        modifier = cardModifier,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (alPulsarChat != null) {
                        Modifier.clickable { alPulsar(paciente) }
                    } else {
                        Modifier
                    },
                )
                .padding(20.dp),
        ) {
            AvatarPerfilCircularApp(
                nombreUsuario = nombreCompleto,
                fotoPerfilUrl = paciente.fotoPerfilUrl,
                tamano = 56.dp,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paciente.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = paciente.apellidos,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        )
        if (alPulsarChat == null) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = if (textoCita != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = textoCita ?: "Sin cita asignada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (textoCita != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { alPulsar(paciente) },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = if (textoCita != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = textoCita ?: "Sin cita asignada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (textoCita != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (tieneAlertaRiesgo) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(percent = 50),
                            color = MaterialTheme.colorScheme.error,
                        ) {
                            Text(
                                text = "Riesgo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    FilledTonalButton(
                        onClick = { alPulsarChat!!(paciente) },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(text = "Chat")
                    }
                    if (tieneNoLeidos) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = RoundedCornerShape(percent = 50),
                                ),
                        )
                    }
                }
            }
        }
    }
}
