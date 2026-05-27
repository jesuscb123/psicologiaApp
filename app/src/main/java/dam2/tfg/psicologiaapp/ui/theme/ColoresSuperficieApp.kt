package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/** Fondo azul suave para tarjetas genéricas (listas, paneles, etc.). */
@Composable
@ReadOnlyComposable
fun colorFondoTarjetaAzulApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainerHigh
    } else {
        scheme.primaryContainer.copy(alpha = 0.18f)
    }
}

/** Variante más marcada para tarjetas activas o destacadas. */
@Composable
@ReadOnlyComposable
fun colorFondoTarjetaAzulActivaApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainerHighest
    } else {
        scheme.primaryContainer.copy(alpha = 0.22f)
    }
}

/** Variante atenuada para tarjetas inactivas o secundarias. */
@Composable
@ReadOnlyComposable
fun colorFondoTarjetaAzulSuaveApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainer
    } else {
        scheme.primaryContainer.copy(alpha = 0.12f)
    }
}

/** Campos de texto y barras de búsqueda con tono azul. */
@Composable
@ReadOnlyComposable
fun colorFondoCampoAzulApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainer
    } else {
        scheme.primaryContainer.copy(alpha = 0.24f)
    }
}

/** Fondo claro para tarjetas de pacientes (home psicólogo). */
@Composable
@ReadOnlyComposable
fun colorFondoTarjetaPacienteApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainerHighest
    } else {
        scheme.surfaceContainerLowest
    }
}

/** Fondo blanco para tarjetas de notas y tareas. */
@Composable
@ReadOnlyComposable
fun colorFondoTarjetaNotasTareasApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainerHighest
    } else {
        scheme.surfaceContainerLowest
    }
}

/** Sub-tarjetas o chips elevados sobre un fondo azul (p. ej. perfil psicólogo). */
@Composable
@ReadOnlyComposable
fun colorFondoSubTarjetaApp(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (esTemaOscuroApp()) {
        scheme.surfaceContainerHighest
    } else {
        scheme.surface
    }
}

/** Tarjeta principal de información del perfil del psicólogo. */
@Composable
@ReadOnlyComposable
fun colorFondoTarjetaInfoPsicologoApp(): Color {
    return if (esTemaOscuroApp()) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        azulTarjetaInfoPerfilClaro
    }
}

/** Fondo del avatar con iniciales cuando no hay foto. */
@Composable
@ReadOnlyComposable
fun colorFondoAvatarInicialApp(): Color {
    return if (esTemaOscuroApp()) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        Color(0xFFEEF2FF)
    }
}

/** Texto del avatar con iniciales. */
@Composable
@ReadOnlyComposable
fun colorTextoAvatarInicialApp(): Color {
    return if (esTemaOscuroApp()) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(0xFF1E3A8A)
    }
}
