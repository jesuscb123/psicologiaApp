package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaApp(
    modifier: Modifier = Modifier,
    paddingContenido: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    elevacion: Dp = 2.dp,
    mostrarBorde: Boolean = true,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val fondoTarjetaAzul = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f)
    val bordeTarjetaAzul = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = fondoTarjetaAzul),
        elevation = CardDefaults.cardElevation(defaultElevation = elevacion),
        border = if (mostrarBorde) {
            BorderStroke(1.dp, bordeTarjetaAzul)
        } else {
            null
        }
    ) {
        Column(modifier = Modifier.padding(paddingContenido)) {
            contenido()
        }
    }
}

