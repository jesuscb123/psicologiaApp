package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Tema oscuro efectivo de la app (puede diferir del sistema si el usuario lo fuerza en ajustes).
 */
val LocalTemaOscuroApp = staticCompositionLocalOf { false }

@Composable
@ReadOnlyComposable
fun esTemaOscuroApp(): Boolean = LocalTemaOscuroApp.current
