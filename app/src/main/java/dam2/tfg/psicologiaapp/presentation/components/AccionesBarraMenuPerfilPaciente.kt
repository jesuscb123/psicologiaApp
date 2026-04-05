package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.AccionesBarraMenuPerfilPaciente(
    nombreUsuario: String,
    fotoPerfilUrl: String?,
    revisionCacheFoto: Long = 0L,
    alAbrirMenu: () -> Unit,
) {
    IconButton(onClick = alAbrirMenu) {
        AvatarPerfilCircularApp(
            nombreUsuario = nombreUsuario,
            fotoPerfilUrl = fotoPerfilUrl,
            tamano = 40.dp,
            revisionCacheFoto = revisionCacheFoto,
        )
    }
}
