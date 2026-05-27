package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.ui.theme.colorFondoSubTarjetaApp

@Composable
fun ChipApp(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = alPulsar,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        label = { Text(text = texto, style = MaterialTheme.typography.labelLarge) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = colorFondoSubTarjetaApp(),
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
fun ChipSeleccionableApp(
    texto: String,
    seleccionado: Boolean,
    alCambiarSeleccion: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = seleccionado,
        onClick = { alCambiarSeleccion(!seleccionado) },
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        label = { Text(text = texto, style = MaterialTheme.typography.labelLarge) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colorFondoSubTarjetaApp(),
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    )
}

