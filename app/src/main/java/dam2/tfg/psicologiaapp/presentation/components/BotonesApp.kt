package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

enum class EstiloBotonTextoApp {
    Normal,
    Enlace,
}

@Composable
fun BotonPrimarioApp(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    cargando: Boolean = false,
    colorContenedor: Color = MaterialTheme.colorScheme.primary,
    colorContenido: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Button(
        onClick = alPulsar,
        enabled = habilitado && !cargando,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorContenedor,
            contentColor = colorContenido,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .fillMaxWidth()
    ) {
        if (cargando) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = colorContenido,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
            )
        }
        Text(text = texto, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun BotonSecundarioApp(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    cargando: Boolean = false,
) {
    OutlinedButton(
        onClick = alPulsar,
        enabled = habilitado && !cargando,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = habilitado && !cargando),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .fillMaxWidth()
    ) {
        if (cargando) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
            )
        }
        Text(text = texto, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun BotonTextoApp(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    estilo: EstiloBotonTextoApp = EstiloBotonTextoApp.Normal,
) {
    val padding = when (estilo) {
        EstiloBotonTextoApp.Normal -> PaddingValues(horizontal = 10.dp, vertical = 8.dp)
        EstiloBotonTextoApp.Enlace -> PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    }
    val estiloTexto: TextStyle = when (estilo) {
        EstiloBotonTextoApp.Normal -> MaterialTheme.typography.titleSmall
        EstiloBotonTextoApp.Enlace -> MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline)
    }

    TextButton(
        onClick = alPulsar,
        enabled = habilitado,
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = padding,
        modifier = modifier
    ) {
        Text(
            text = texto,
            style = estiloTexto,
            color = if (estilo == EstiloBotonTextoApp.Enlace) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Unspecified
            },
        )
    }
}

