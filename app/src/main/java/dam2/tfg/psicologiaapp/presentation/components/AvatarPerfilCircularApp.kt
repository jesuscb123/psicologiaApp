package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun AvatarPerfilCircularApp(
    nombreUsuario: String,
    fotoPerfilUrl: String?,
    modifier: Modifier = Modifier,
    tamano: Dp = 40.dp,
    revisionCacheFoto: Long = 0L,
) {
    val url = fotoPerfilUrl?.trim()?.takeIf { it.isNotEmpty() }
    val contexto = LocalContext.current
    val contenedor = modifier
        .size(tamano)
        .clip(CircleShape)
    if (url != null) {
        val solicitudImagen = remember(url, revisionCacheFoto, contexto) {
            ImageRequest.Builder(contexto)
                .data(url)
                .memoryCacheKey("${url}_$revisionCacheFoto")
                .diskCacheKey("${url}_$revisionCacheFoto")
                .crossfade(true)
                .build()
        }
        SubcomposeAsyncImage(
            model = solicitudImagen,
            contentDescription = "Foto de perfil de $nombreUsuario",
            modifier = contenedor,
            contentScale = ContentScale.Crop,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AvatarInicialApp(
                        nombre = nombreUsuario,
                        tamano = tamano * 0.85f,
                    )
                }
            },
            error = {
                AvatarInicialApp(
                    nombre = nombreUsuario,
                    modifier = Modifier.fillMaxSize(),
                    tamano = tamano,
                )
            },
            success = { SubcomposeAsyncImageContent() },
        )
    } else {
        AvatarInicialApp(
            nombre = nombreUsuario,
            modifier = contenedor,
            tamano = tamano,
        )
    }
}
