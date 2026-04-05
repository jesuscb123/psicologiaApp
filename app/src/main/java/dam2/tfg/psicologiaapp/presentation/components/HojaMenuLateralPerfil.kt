package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp

@Composable
fun HojaMenuLateralPerfil(
    nombreUsuario: String,
    fotoPerfilUrl: String?,
    revisionCacheFoto: Long = 0L,
    modoTema: ModoTemaApp,
    temaOscuroResuelto: Boolean,
    modifier: Modifier = Modifier,
    mensajeError: String? = null,
    cargandoFotoPerfil: Boolean = false,
    alPulsarFotoPerfil: () -> Unit = {},
    alFijarModoTema: (ModoTemaApp) -> Unit,
    alIrAjustes: () -> Unit,
    alAcercaDe: () -> Unit,
    alCerrarSesion: () -> Unit,
) {
    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
        ) {
            val descripcionEditarFoto = "Cambiar foto de perfil"
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = descripcionEditarFoto,
                        role = Role.Button,
                        onClick = alPulsarFotoPerfil,
                    ),
            ) {
                AvatarPerfilCircularApp(
                    nombreUsuario = nombreUsuario,
                    fotoPerfilUrl = fotoPerfilUrl,
                    tamano = 72.dp,
                    revisionCacheFoto = revisionCacheFoto,
                )
                if (cargandoFotoPerfil) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp),
                        strokeWidth = 3.dp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            mensajeError?.let { texto ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = texto,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { Text("Ajustes") },
                selected = false,
                onClick = alIrAjustes,
            )

            FilaModoOscuroMenuLateral(
                temaOscuroResuelto = temaOscuroResuelto,
                alCambiarOscuro = { activar ->
                    alFijarModoTema(if (activar) ModoTemaApp.Oscuro else ModoTemaApp.Claro)
                },
            )

            NavigationDrawerItem(
                label = { Text("Usar tema del dispositivo") },
                selected = modoTema == ModoTemaApp.SeguirSistema,
                onClick = { alFijarModoTema(ModoTemaApp.SeguirSistema) },
            )

            NavigationDrawerItem(
                label = { Text("Acerca de") },
                selected = false,
                onClick = alAcercaDe,
            )

            NavigationDrawerItem(
                label = { Text("Cerrar sesión") },
                selected = false,
                onClick = alCerrarSesion,
            )
        }
    }
}

@Composable
private fun FilaModoOscuroMenuLateral(
    temaOscuroResuelto: Boolean,
    alCambiarOscuro: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Modo oscuro",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = temaOscuroResuelto,
            onCheckedChange = alCambiarOscuro,
        )
    }
}
