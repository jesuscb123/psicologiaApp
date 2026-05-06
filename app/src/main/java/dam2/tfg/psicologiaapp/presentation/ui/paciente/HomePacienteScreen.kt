package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.presentation.components.AvatarPerfilCircularApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomePaciente(
    alIrAPerfilPsicologo: (String) -> Unit,
    alIrANotas: () -> Unit,
    alIrATareas: () -> Unit,
    alIrACitas: () -> Unit,
    alIrAAjustes: () -> Unit,
    alIrAChat: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: HomePacienteViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.recargar()
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        val psicologoIdAsignado = uiState.perfilPaciente?.psicologoId

        PantallaConCabeceraOndaApp(
            encabezado = {
                EncabezadoUsuarioApp(
                    mostrarIconoMenu = true,
                    alAbrirMenu = alAbrirMenuPerfil,
                    nombreUsuario = nombreUsuarioBarra,
                    fotoPerfilUrl = fotoPerfilUrlBarra,
                    revisionCacheFoto = revisionCacheFotoBarra,
                    alAbrirMenuPerfil = alAbrirMenuPerfil,
                )
            },
            modifier = Modifier.fillMaxSize(),
            paddingContenido = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        ) {
            when {
                uiState.cargando -> {
                    HomePacienteContenidoCargando(mensajeError = uiState.mensajeError)
                }

                psicologoIdAsignado == null -> {
                    HomePacienteSeleccionPsicologo(
                        mensajeError = uiState.mensajeError,
                        psicologos = uiState.listaPsicologos,
                        alIrAPerfilPsicologo = alIrAPerfilPsicologo,
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        uiState.mensajeError?.let { msg ->
                            item {
                                Text(
                                    text = msg,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }

                        item {
                            BannerProximaCitaPaciente(
                                proximaCita = uiState.proximaCita,
                                alVerCitas = alIrACitas,
                            )
                        }

                        item {
                            HomePacienteDashboardGrid(
                                alIrAMisNotas = alIrANotas,
                                alIrAMisTareas = alIrATareas,
                                alIrACitas = alIrACitas,
                                alIrAAjustes = alIrAAjustes,
                            )
                        }

                        item {
                            BannerPsicologoActualPaciente(
                                psicologo = uiState.psicologoAsignado,
                                alVerPerfil = alIrAPerfilPsicologo,
                                alEnviarMensaje = alIrAChat,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePacienteContenidoCargando(mensajeError: String?) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 32.dp),
        ) {
            mensajeError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "Cargando…",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomePacienteSeleccionPsicologo(
    mensajeError: String?,
    psicologos: List<Psicologo>,
    alIrAPerfilPsicologo: (String) -> Unit,
) {
    var textoBusqueda by rememberSaveable { mutableStateOf("") }
    val psicologosFiltrados = remember(psicologos, textoBusqueda) {
        val q = textoBusqueda.trim().lowercase()
        if (q.isEmpty()) {
            psicologos
        } else {
            psicologos.filter { p ->
                p.nombre.lowercase().contains(q) ||
                    p.apellidos.lowercase().contains(q) ||
                    p.especialidad.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        mensajeError?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Encuentra tu",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "espacio seguro.",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ),
                ),
            )
            Text(
                text = "Explora nuestra red de profesionales. Elige a la persona que te acompañe en tu proceso, a tu ritmo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Buscar por especialidad o nombre…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        )

        Text(
            text = "Profesionales disponibles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (psicologosFiltrados.isEmpty()) {
                Text(
                    text = if (textoBusqueda.isNotBlank()) {
                        "No hay profesionales que coincidan con tu búsqueda."
                    } else {
                        "No hay profesionales disponibles por ahora."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(
                        items = psicologosFiltrados,
                        key = { it.idEntidadPsicologo },
                    ) { psicologo ->
                        FilaPsicologoApp(
                            psicologo = psicologo,
                            alPulsar = { alIrAPerfilPsicologo(it.usuarioId.toString()) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerProximaCitaPaciente(
    proximaCita: Cita?,
    alVerCitas: () -> Unit,
) {
    val sub = when {
        proximaCita != null -> {
            val fechaHora = formatearInicioCitaBanner(proximaCita.inicio)
            "$fechaHora · ${proximaCita.nombrePsicologo}"
        }
        else -> "No tienes citas próximas. Consulta o reserva desde Mis citas."
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer,
                    ),
                ),
            )
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        .padding(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Column {
                    Text(
                        text = "Tus citas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        maxLines = 2,
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(percent = 50),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable(onClick = alVerCitas),
            ) {
                Text(
                    text = "Gestionar",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

private fun formatearInicioCitaBanner(isoOffset: String): String =
    runCatching {
        val instant = OffsetDateTime.parse(isoOffset).toInstant()
        instant.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }.getOrElse { isoOffset }

@Composable
private fun HomePacienteDashboardGrid(
    alIrAMisNotas: () -> Unit,
    alIrAMisTareas: () -> Unit,
    alIrACitas: () -> Unit,
    alIrAAjustes: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaAccesoPacienteApp(
                titulo = "Mis notas",
                subtitulo = "Consulta y escribe tus notas.",
                icono = Icons.Filled.Info,
                onClick = alIrAMisNotas,
                modifier = Modifier.weight(1f),
            )
            TarjetaAccesoPacienteApp(
                titulo = "Mis tareas",
                subtitulo = "Sigue tus tareas terapéuticas.",
                icono = Icons.Filled.CheckCircle,
                onClick = alIrAMisTareas,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaAccesoPacienteApp(
                titulo = "Citas",
                subtitulo = "Gestiona tus próximas sesiones.",
                icono = Icons.Filled.DateRange,
                onClick = alIrACitas,
                modifier = Modifier.weight(1f),
            )
            TarjetaAccesoPacienteApp(
                titulo = "Ajustes",
                subtitulo = "Configura tu cuenta y tema.",
                icono = Icons.Filled.Settings,
                onClick = alIrAAjustes,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TarjetaAccesoPacienteApp(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(10.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun BannerPsicologoActualPaciente(
    psicologo: Psicologo?,
    alVerPerfil: (String) -> Unit,
    alEnviarMensaje: () -> Unit,
) {
    val nombreCompleto = psicologo?.let {
        listOf(it.nombre, it.apellidos).filter { p -> p.isNotBlank() }.joinToString(" ")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer,
                    ),
                ),
            )
            .padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    AvatarPerfilCircularApp(
                        nombreUsuario = nombreCompleto ?: "",
                        fotoPerfilUrl = psicologo?.fotoPerfilUrl,
                        tamano = 44.dp,
                    )
                    Column {
                        Text(
                            text = "Psicólogo actual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            text = nombreCompleto ?: "Sin especialista asignado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            maxLines = 2,
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .then(
                            if (psicologo != null) {
                                Modifier.clickable { alVerPerfil(psicologo.usuarioId.toString()) }
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    Text(
                        text = "Ver perfil",
                        color = if (psicologo != null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }

            if (psicologo != null) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .wrapContentWidth()
                        .widthIn(max = 220.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { alEnviarMensaje() },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Enviar mensaje",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaPsicologoApp(
    psicologo: Psicologo,
    alPulsar: (Psicologo) -> Unit,
) {
    val nombreCompleto = listOf(psicologo.nombre, psicologo.apellidos)
        .filter { it.isNotBlank() }
        .joinToString(" ")
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { alPulsar(psicologo) },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarPerfilCircularApp(
                nombreUsuario = nombreCompleto,
                fotoPerfilUrl = psicologo.fotoPerfilUrl,
                tamano = 56.dp,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = nombreCompleto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (psicologo.especialidad.isNotBlank()) {
                    Text(
                        text = psicologo.especialidad,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

