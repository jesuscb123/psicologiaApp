package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dam2.tfg.psicologiaapp.ui.theme.colorFondoAvatarInicialApp
import dam2.tfg.psicologiaapp.ui.theme.colorTextoAvatarInicialApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AvatarInicialApp(
    nombre: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 44.dp,
) {
    val inicial = nombre.trim().firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(colorFondoAvatarInicialApp())
    ) {
        Text(
            text = inicial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorTextoAvatarInicialApp(),
            modifier = Modifier.padding((tamano.value / 4).dp)
        )
    }
}

