package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaApp(
    modifier: Modifier = Modifier,
    paddingContenido: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    mostrarBorde: Boolean = true,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    TarjetaSuperficieAzulApp(
        modifier = modifier,
        mostrarBorde = mostrarBorde,
    ) {
        Column(modifier = Modifier.padding(paddingContenido)) {
            contenido()
        }
    }
}
