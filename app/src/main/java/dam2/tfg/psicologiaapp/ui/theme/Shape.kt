package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Sistema de radios coherente para toda la app:
 * - **Cards**: suaves y amplias
 * - **Botones/Chips**: estilo "pill"
 * - **Inputs**: redondeo cómodo sin llegar a cápsula total
 */
val FormasPsicologiaApp = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

