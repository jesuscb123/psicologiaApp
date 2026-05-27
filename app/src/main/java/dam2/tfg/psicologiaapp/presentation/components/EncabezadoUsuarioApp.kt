package dam2.tfg.psicologiaapp.presentation.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val MARCA_APP = "Acompáñame"
private val anchoAccionEncabezado = 48.dp
private val alturaContenidoEncabezado = 48.dp
private val margenHorizontalEncabezado = 16.dp

@Composable
fun EncabezadoUsuarioApp(
    nombreUsuario: String,
    fotoPerfilUrl: String?,
    revisionCacheFoto: Long = 0L,
    alAbrirMenuPerfil: () -> Unit,
    modifier: Modifier = Modifier,
    tituloCentro: String = MARCA_APP,
    mostrarIconoMenu: Boolean = false,
    alAbrirMenu: (() -> Unit)? = null,
    mostrarFlechaAtras: Boolean = false,
    alVolver: (() -> Unit)? = null,
    sobreGradiente: Boolean = false,
) {
    val dispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val cabeceraSobreGradiente = LocalCabeceraSobreGradiente.current || sobreGradiente
    val colorTextoPrincipal = colorTextoEncabezado(cabeceraSobreGradiente, principal = true)
    val accionMenu = alAbrirMenu ?: alAbrirMenuPerfil
    val esTituloMarca = tituloCentro == MARCA_APP

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(alturaContenidoEncabezado)
            .padding(horizontal = margenHorizontalEncabezado),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Box(
            modifier = Modifier.size(anchoAccionEncabezado),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (mostrarIconoMenu) {
                BotonNavegacionEncabezado(
                    onClick = accionMenu,
                    contentDescription = "Abrir menú",
                    icon = Icons.Filled.Menu,
                    colorContenido = colorTextoPrincipal,
                    sobreGradiente = cabeceraSobreGradiente,
                    iconoTransparente = cabeceraSobreGradiente,
                )
            } else if (mostrarFlechaAtras) {
                val accionVolver: () -> Unit =
                    alVolver ?: { dispatcher?.onBackPressed() }
                BotonNavegacionEncabezado(
                    onClick = accionVolver,
                    contentDescription = "Volver",
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    colorContenido = colorTextoPrincipal,
                    sobreGradiente = cabeceraSobreGradiente,
                    iconoTransparente = cabeceraSobreGradiente,
                )
            }
        }

        Text(
            text = tituloCentro,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            style = if (cabeceraSobreGradiente) {
                MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = if (esTituloMarca) FontWeight.Bold else FontWeight.SemiBold,
                )
            } else {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                )
            },
            textAlign = TextAlign.Center,
            color = colorTextoPrincipal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Box(
            modifier = Modifier.size(anchoAccionEncabezado),
            contentAlignment = Alignment.CenterEnd,
        ) {
            AccionesBarraMenuPerfilPaciente(
                nombreUsuario = nombreUsuario,
                fotoPerfilUrl = fotoPerfilUrl,
                revisionCacheFoto = revisionCacheFoto,
                alAbrirMenu = alAbrirMenuPerfil,
            )
        }
    }
}

@Composable
private fun BotonNavegacionEncabezado(
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector,
    colorContenido: Color,
    sobreGradiente: Boolean,
    iconoTransparente: Boolean = false,
) {
    if (iconoTransparente) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(anchoAccionEncabezado),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = colorContenido,
            ),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
        return
    }

    Surface(
        shape = CircleShape,
        color = if (sobreGradiente) {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = colorContenido,
            ),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
private fun colorTextoEncabezado(sobreGradiente: Boolean, principal: Boolean): Color {
    return if (sobreGradiente) {
        if (principal) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
        }
    } else {
        if (principal) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
}
