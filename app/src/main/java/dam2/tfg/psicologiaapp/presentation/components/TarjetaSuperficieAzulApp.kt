package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import dam2.tfg.psicologiaapp.ui.theme.colorFondoTarjetaAzulApp

/**
 * Contenedor de tarjeta con fondo azul uniforme y borde, sin elevación de Material
 * (evita el rectángulo claro interno del surface tint).
 */
@Composable
fun TarjetaSuperficieAzulApp(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    colorFondo: Color = colorFondoTarjetaAzulApp(),
    mostrarBorde: Boolean = true,
    alphaBorde: Float = 0.16f,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val colorBorde = MaterialTheme.colorScheme.primary.copy(alpha = alphaBorde)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colorFondo)
            .then(
                if (mostrarBorde) {
                    Modifier.border(width = 1.dp, color = colorBorde, shape = shape)
                } else {
                    Modifier
                },
            ),
    ) {
        Column(content = contenido)
    }
}
