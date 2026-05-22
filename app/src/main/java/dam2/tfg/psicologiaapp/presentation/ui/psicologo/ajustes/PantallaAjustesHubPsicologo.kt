package dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.presentation.components.ChipSeleccionableApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

@Composable
fun PantallaAjustesHubPsicologo(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    modoTema: ModoTemaApp,
    alFijarModoTema: (ModoTemaApp) -> Unit,
    alIrModificarPerfil: () -> Unit,
    alIrAcercaDe: () -> Unit,
    alCerrarSesion: () -> Unit,
) {
    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Ajustes",
                mostrarFlechaAtras = true,
                alVolver = alVolver,
                nombreUsuario = nombreUsuarioBarra,
                fotoPerfilUrl = fotoPerfilUrlBarra,
                revisionCacheFoto = revisionCacheFotoBarra,
                alAbrirMenuPerfil = alAbrirMenuPerfil,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Tema de la aplicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Elige cómo quieres que se vea la aplicación.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipSeleccionableApp(
                            texto = "Sistema",
                            seleccionado = modoTema == ModoTemaApp.SeguirSistema,
                            alCambiarSeleccion = { if (it) alFijarModoTema(ModoTemaApp.SeguirSistema) },
                        )
                        ChipSeleccionableApp(
                            texto = "Claro",
                            seleccionado = modoTema == ModoTemaApp.Claro,
                            alCambiarSeleccion = { if (it) alFijarModoTema(ModoTemaApp.Claro) },
                        )
                        ChipSeleccionableApp(
                            texto = "Oscuro",
                            seleccionado = modoTema == ModoTemaApp.Oscuro,
                            alCambiarSeleccion = { if (it) alFijarModoTema(ModoTemaApp.Oscuro) },
                        )
                    }
                }
            }

            Text(
                text = "General",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    FilaAjustePsicologo(
                        texto = "Modificar perfil",
                        icono = Icons.Filled.Person,
                        onClick = alIrModificarPerfil,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                    FilaAjustePsicologo(
                        texto = "Acerca de",
                        icono = Icons.Filled.Info,
                        onClick = alIrAcercaDe,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                    FilaAjustePsicologo(
                        texto = "Cerrar sesión",
                        icono = Icons.AutoMirrored.Filled.ExitToApp,
                        onClick = alCerrarSesion,
                        colorTexto = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaAjustePsicologo(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit,
    colorTexto: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorTexto,
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = colorTexto,
        )
    }
}
