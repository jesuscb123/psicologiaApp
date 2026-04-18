package dam2.tfg.psicologiaapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val esquemaColoresOscuro = darkColorScheme(
    primary = primarioOscuro,
    onPrimary = enPrimarioOscuro,
    primaryContainer = contenedorPrimarioOscuro,
    onPrimaryContainer = enContenedorPrimarioOscuro,
    secondary = secundarioOscuro,
    onSecondary = enSecundarioOscuro,
    secondaryContainer = contenedorSecundarioOscuro,
    onSecondaryContainer = enContenedorSecundarioOscuro,
    tertiary = terciarioOscuro,
    onTertiary = enTerciarioOscuro,
    tertiaryContainer = contenedorTerciarioOscuro,
    onTertiaryContainer = enContenedorTerciarioOscuro,
    background = fondoOscuro,
    onBackground = enFondoOscuro,
    surface = superficieOscuro,
    onSurface = enSuperficieOscuro,
    surfaceVariant = varianteSuperficieOscuro,
    onSurfaceVariant = enVarianteSuperficieOscuro,
    surfaceDim = superficieDimOscuro,
    surfaceBright = superficieBrillanteOscuro,
    surfaceContainerLowest = superficieContenedorMinimaOscuro,
    surfaceContainerLow = superficieContenedorBajaOscuro,
    surfaceContainer = superficieContenedorOscuro,
    surfaceContainerHigh = superficieContenedorAltaOscuro,
    surfaceContainerHighest = superficieContenedorMaximaOscuro,
    outline = contornoOscuro,
    outlineVariant = contornoVarianteOscuro,
    error = errorOscuro,
    onError = enErrorOscuro,
    errorContainer = contenedorErrorOscuro,
    onErrorContainer = enContenedorErrorOscuro,
    inverseSurface = superficieInversaOscuro,
    inverseOnSurface = enSuperficieInversaOscuro,
    inversePrimary = primarioInversoOscuro,
    surfaceTint = primarioOscuro,
    scrim = fondoOscuro
)

private val esquemaColoresClaro = lightColorScheme(
    primary = primarioClaro,
    onPrimary = enPrimarioClaro,
    primaryContainer = contenedorPrimarioClaro,
    onPrimaryContainer = enContenedorPrimarioClaro,
    secondary = secundarioClaro,
    onSecondary = enSecundarioClaro,
    secondaryContainer = contenedorSecundarioClaro,
    onSecondaryContainer = enContenedorSecundarioClaro,
    tertiary = terciarioClaro,
    onTertiary = enTerciarioClaro,
    tertiaryContainer = contenedorTerciarioClaro,
    onTertiaryContainer = enContenedorTerciarioClaro,
    background = fondoClaro,
    onBackground = enFondoClaro,
    surface = superficieClaro,
    onSurface = enSuperficieClaro,
    surfaceVariant = varianteSuperficieClaro,
    onSurfaceVariant = enVarianteSuperficieClaro,
    surfaceDim = superficieDimClaro,
    surfaceBright = superficieBrillanteClaro,
    surfaceContainerLowest = superficieContenedorMinimaClaro,
    surfaceContainerLow = superficieContenedorBajaClaro,
    surfaceContainer = superficieContenedorClaro,
    surfaceContainerHigh = superficieContenedorAltaClaro,
    surfaceContainerHighest = superficieContenedorMaximaClaro,
    outline = contornoClaro,
    outlineVariant = contornoVarianteClaro,
    error = errorClaro,
    onError = enErrorClaro,
    errorContainer = contenedorErrorClaro,
    onErrorContainer = enContenedorErrorClaro,
    inverseSurface = superficieInversaClaro,
    inverseOnSurface = enSuperficieInversaClaro,
    inversePrimary = primarioInversoClaro,
    surfaceTint = primarioClaro,
    scrim = Color(0xFF000000)
)

@Composable
fun PsicologiaappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> esquemaColoresOscuro
        else -> esquemaColoresClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FormasPsicologiaApp,
        content = content
    )
}
