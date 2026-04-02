package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorApp(
    titulo: String,
    subtitulo: String? = null,
    textoVolver: String = "Volver",
    alVolver: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Column {
                Text(text = titulo)
                if (!subtitulo.isNullOrBlank()) {
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            if (alVolver != null) {
                TextButton(onClick = alVolver) { Text(textoVolver) }
            }
        }
    )
}

