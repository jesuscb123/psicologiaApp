package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorApp(
    titulo: String,
    subtitulo: String? = null,
    textoVolver: String = "Volver",
    alVolver: (() -> Unit)? = null,
    acciones: (@Composable RowScope.() -> Unit)? = null,
    mostrarAvatarJuntoTitulo: Boolean = false,
    fotoPerfilUrlAvatarTitulo: String? = null,
    revisionCacheFotoAvatarTitulo: Long = 0L,
) {
    TopAppBar(
        title = {
            if (mostrarAvatarJuntoTitulo && titulo.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AvatarPerfilCircularApp(
                        nombreUsuario = titulo,
                        fotoPerfilUrl = fotoPerfilUrlAvatarTitulo,
                        tamano = 40.dp,
                        revisionCacheFoto = revisionCacheFotoAvatarTitulo,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = titulo, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!subtitulo.isNullOrBlank()) {
                            Text(
                                text = subtitulo,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            } else {
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
            }
        },
        navigationIcon = {
            if (alVolver != null) {
                TextButton(onClick = alVolver) { Text(textoVolver) }
            }
        },
        actions = { acciones?.invoke(this) },
    )
}

