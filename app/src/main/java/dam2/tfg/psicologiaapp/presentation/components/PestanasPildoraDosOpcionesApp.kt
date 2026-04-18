package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Segmento de dos pestañas estilo píldora (design tipo Stitch / “Digital Sanctuary”):
 * fondo [surfaceContainerHigh] y pestaña activa elevada sobre [surface].
 */
@Composable
fun PestanasPildoraDosOpcionesApp(
    primeraEtiqueta: String,
    segundaEtiqueta: String,
    indiceSeleccionado: Int,
    alSeleccionarPrimera: () -> Unit,
    alSeleccionarSegunda: () -> Unit,
    modifier: Modifier = Modifier,
) {
    require(indiceSeleccionado in 0..1) { "indiceSeleccionado debe ser 0 o 1" }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PildoraOpcion(
                texto = primeraEtiqueta,
                seleccionada = indiceSeleccionado == 0,
                onClick = alSeleccionarPrimera,
                modifier = Modifier.weight(1f),
            )
            PildoraOpcion(
                texto = segundaEtiqueta,
                seleccionada = indiceSeleccionado == 1,
                onClick = alSeleccionarSegunda,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PildoraOpcion(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = if (seleccionada) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0f)
        },
        tonalElevation = if (seleccionada) 3.dp else 0.dp,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (seleccionada) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        )
    }
}
