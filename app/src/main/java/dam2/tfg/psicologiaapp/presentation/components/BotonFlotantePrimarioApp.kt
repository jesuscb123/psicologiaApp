package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dam2.tfg.psicologiaapp.ui.theme.brushGradienteFirma

@Composable
fun BotonFlotantePrimarioApp(
    alPulsar: () -> Unit,
    descripcionIcono: String,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Filled.Add,
) {
    FloatingActionButton(
        onClick = alPulsar,
        modifier = modifier.background(
            brush = brushGradienteFirma(),
            shape = CircleShape,
        ),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(),
    ) {
        Icon(imageVector = icono, contentDescription = descripcionIcono)
    }
}
