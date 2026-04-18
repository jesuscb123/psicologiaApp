package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

/**
 * Gradiente firma del prototipo: 135° desde `primary` hasta `primaryContainer`
 * (login, botones primarios, héroes). El vector largo cubre bien superficies anchas;
 * en elementos muy grandes se puede usar [brushGradienteFirmaEn] con el tamaño medido.
 */
@Composable
@ReadOnlyComposable
fun brushGradienteFirma(): Brush {
    val scheme = MaterialTheme.colorScheme
    return Brush.linearGradient(
        colors = listOf(
            scheme.primary,
            scheme.primaryContainer
        ),
        start = Offset.Zero,
        end = Offset(1000f, 1000f)
    )
}

fun brushGradienteFirmaEn(
    primario: Color,
    contenedorPrimario: Color,
    anchoPx: Float,
    altoPx: Float
): Brush = Brush.linearGradient(
    colors = listOf(primario, contenedorPrimario),
    start = Offset.Zero,
    end = Offset(anchoPx, altoPx)
)

/** Sombra ambiental: `onSurface` al 6 %, para `Modifier.shadow` o tonalidad suave. */
fun sombraAmbientalColor(onSurface: Color): Color = onSurface.copy(alpha = 0.06f)
