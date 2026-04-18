package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Radios del `borderRadius` extendido en Tailwind del prototipo: default 1rem, input ~1.5rem, card lg 2rem, xl 3rem.
 */
val FormasPsicologiaApp = Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(48.dp)
)
