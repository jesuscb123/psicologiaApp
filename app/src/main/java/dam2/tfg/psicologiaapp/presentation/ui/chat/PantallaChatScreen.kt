package dam2.tfg.psicologiaapp.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.presentation.components.AvatarPerfilCircularApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChatScreen(
    alVolver: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.rtdbRuta) {
        if (uiState.rtdbRuta != null) {
            viewModel.marcarLeido()
        }
    }

    LaunchedEffect(uiState.mensajes.size) {
        if (uiState.mensajes.isNotEmpty()) {
            listState.animateScrollToItem(uiState.mensajes.size - 1)
        }
    }

    val interlocutorNombreCompleto = remember(uiState.interlocutorNombre, uiState.interlocutorApellidos) {
        listOf(uiState.interlocutorNombre, uiState.interlocutorApellidos)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    // El Scaffold raíz (AppNavHost) ya gestiona todos los insets del sistema (safeDrawing incluye
    // status bar, nav bar e IME). No se añade ningún padding de insets aquí para evitar duplicar
    // la compensación del teclado.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        CabeceraChatApp(
            nombreInterlocutor = interlocutorNombreCompleto,
            fotoPerfilUrl = uiState.interlocutorFotoPerfilUrl,
            alVolver = alVolver,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when {
                uiState.cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                uiState.mensajeError != null && uiState.rtdbRuta == null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = uiState.mensajeError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        TextButton(onClick = viewModel::reintentar) {
                            Text("Reintentar")
                        }
                    }
                }

                uiState.mensajes.isEmpty() -> {
                    Text(
                        text = "Aún no hay mensajes. ¡Empieza la conversación!",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        itemsIndexed(
                            items = uiState.mensajes,
                            key = { _, mensaje -> mensaje.id },
                        ) { index, mensaje ->
                            val esMio = mensaje.remitenteUid == uiState.uidActual
                            val siguiente = uiState.mensajes.getOrNull(index + 1)
                            val mostrarAvatar = siguiente == null ||
                                siguiente.remitenteUid != mensaje.remitenteUid
                            BurbujaMensajeChatApp(
                                mensaje = mensaje,
                                esMio = esMio,
                                nombreRemitente = if (esMio) nombreUsuarioBarra else interlocutorNombreCompleto,
                                fotoPerfilUrl = if (esMio) fotoPerfilUrlBarra else uiState.interlocutorFotoPerfilUrl,
                                revisionCacheFoto = if (esMio) revisionCacheFotoBarra else 0L,
                                mostrarAvatar = mostrarAvatar,
                            )
                        }
                    }
                }
            }
        }

        if (uiState.mensajeError != null && uiState.rtdbRuta != null) {
            Text(
                text = uiState.mensajeError!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        HorizontalDivider()
        BarraEntradaMensajeChatApp(
            texto = uiState.textoActual,
            enviando = uiState.enviando,
            habilitado = uiState.rtdbRuta != null && !uiState.cargando,
            alCambiarTexto = viewModel::actualizarTexto,
            alEnviar = viewModel::enviarMensaje,
        )
    }
}

@Composable
private fun CabeceraChatApp(
    nombreInterlocutor: String,
    fotoPerfilUrl: String?,
    alVolver: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = alVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            AvatarPerfilCircularApp(
                nombreUsuario = nombreInterlocutor,
                fotoPerfilUrl = fotoPerfilUrl,
                tamano = 40.dp,
            )
            Text(
                text = nombreInterlocutor.ifBlank { "Chat" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BarraEntradaMensajeChatApp(
    texto: String,
    enviando: Boolean,
    habilitado: Boolean,
    alCambiarTexto: (String) -> Unit,
    alEnviar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = alCambiarTexto,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Escribe un mensaje…") },
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            enabled = habilitado,
        )
        FilledIconButton(
            onClick = alEnviar,
            enabled = texto.trim().isNotBlank() && habilitado && !enviando,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            if (enviando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                )
            }
        }
    }
}

@Composable
private fun BurbujaMensajeChatApp(
    mensaje: MensajeChat,
    esMio: Boolean,
    nombreRemitente: String,
    fotoPerfilUrl: String?,
    revisionCacheFoto: Long,
    mostrarAvatar: Boolean,
) {
    val alineacion = if (esMio) Arrangement.End else Arrangement.Start
    val colorFondo = if (esMio) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val colorTexto = if (esMio) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val forma = if (esMio) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }
    val hora = remember(mensaje.enviadoEn) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(mensaje.enviadoEn))
    }

    val tamanoAvatar = 32.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alineacion,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!esMio) {
            if (mostrarAvatar) {
                AvatarPerfilCircularApp(
                    nombreUsuario = nombreRemitente,
                    fotoPerfilUrl = fotoPerfilUrl,
                    tamano = tamanoAvatar,
                    revisionCacheFoto = revisionCacheFoto,
                )
            } else {
                Spacer(Modifier.size(tamanoAvatar))
            }
            Spacer(Modifier.size(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (esMio) Alignment.End else Alignment.Start,
        ) {
            Surface(
                color = colorFondo,
                shape = forma,
            ) {
                Text(
                    text = mensaje.texto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorTexto,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
            Text(
                text = hora,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }

        if (esMio) {
            Spacer(Modifier.size(8.dp))
            if (mostrarAvatar) {
                AvatarPerfilCircularApp(
                    nombreUsuario = nombreRemitente,
                    fotoPerfilUrl = fotoPerfilUrl,
                    tamano = tamanoAvatar,
                    revisionCacheFoto = revisionCacheFoto,
                )
            } else {
                Spacer(Modifier.size(tamanoAvatar))
            }
        }
    }
}
