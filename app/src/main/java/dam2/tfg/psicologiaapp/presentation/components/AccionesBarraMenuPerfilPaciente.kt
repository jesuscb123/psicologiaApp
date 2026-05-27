package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AccionesBarraMenuPerfilPaciente(
    nombreUsuario: String,
    fotoPerfilUrl: String?,
    revisionCacheFoto: Long = 0L,
    alAbrirMenu: () -> Unit,
) {
    val cabeceraSobreGradiente = LocalCabeceraSobreGradiente.current

    IconButton(
        onClick = alAbrirMenu,
        modifier = Modifier.size(48.dp),
    ) {
        if (cabeceraSobreGradiente) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                AvatarPerfilCircularApp(
                    nombreUsuario = nombreUsuario,
                    fotoPerfilUrl = fotoPerfilUrl,
                    tamano = 36.dp,
                    revisionCacheFoto = revisionCacheFoto,
                )
            }
        } else {
            AvatarPerfilCircularApp(
                nombreUsuario = nombreUsuario,
                fotoPerfilUrl = fotoPerfilUrl,
                tamano = 36.dp,
                revisionCacheFoto = revisionCacheFoto,
            )
        }
    }
}
