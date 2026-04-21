package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.AvatarPerfilCircularApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasPacienteApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class PestanaHomePaciente {
    NOTAS,
    TAREAS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomePaciente(
    alIrAPerfilPsicologo: (String) -> Unit,
    alIrAAnadirNota: () -> Unit,
    alIrACitas: () -> Unit,
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
    var notaPendienteEliminar by remember { mutableStateOf<Nota?>(null) }
    var tareaIdDialogo by remember { mutableStateOf<Long?>(null) }

    val tareaParaDialogo = tareaIdDialogo?.let { id ->
        uiState.tareas.find { it.id == id }
    }

    tareaParaDialogo?.let { tarea ->
        AlertDialog(
            onDismissRequest = { tareaIdDialogo = null },
            title = { Text(tarea.titulo) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tarea.descripcion)
                    if (!tarea.aceptadaPorPaciente) {
                        Text(
                            text = "Al aceptarla podrás marcarla como completada cuando la hayas hecho.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
            confirmButton = {
                when {
                    !tarea.aceptadaPorPaciente ->
                        TextButton(
                            onClick = {
                                viewModel.aceptarTarea(tarea.id)
                                tareaIdDialogo = null
                            }
                        ) { Text("Aceptar") }
                    tarea.aceptadaPorPaciente && !tarea.realizada ->
                        TextButton(
                            onClick = {
                                viewModel.marcarTareaRealizada(tarea.id, true)
                                tareaIdDialogo = null
                            }
                        ) { Text("Marcar como completada") }
                    else ->
                        TextButton(onClick = { tareaIdDialogo = null }) { Text("Cerrar") }
                }
            },
            dismissButton = {
                if (!tarea.realizada) {
                    TextButton(onClick = { tareaIdDialogo = null }) { Text("Cerrar") }
                }
            },
        )
    }

    notaPendienteEliminar?.let { nota ->
        AlertDialog(
            onDismissRequest = { notaPendienteEliminar = null },
            title = { Text("Eliminar nota") },
            text = {
                Text(
                    "¿Seguro que quieres eliminar la nota «${nota.asunto}»? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarNota(nota.id)
                        notaPendienteEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { notaPendienteEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val psicologoIdAsignado = uiState.perfilPaciente?.psicologoId
        var pestanaActual by rememberSaveable(psicologoIdAsignado) {
            mutableStateOf(PestanaHomePaciente.NOTAS)
        }

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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(bottom = 88.dp),
                    ) {
                        uiState.mensajeError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        BannerProximaCitaPaciente(
                            proximaCita = uiState.proximaCita,
                            alVerCitas = alIrACitas,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PestanasEstiloProto(
                            pestanaActual = pestanaActual,
                            alCambiarPestana = { pestanaActual = it },
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            when (pestanaActual) {
                                PestanaHomePaciente.NOTAS -> {
                                    if (uiState.notas.isEmpty()) {
                                        TextoEstadoVacioHomePaciente(
                                            texto = "Todavía no hay notas. Cuando escribas algo con tu terapeuta, aparecerá aquí.",
                                        )
                                    } else {
                                        ListaNotasApp(
                                            notas = uiState.notas,
                                            alSolicitarEliminar = { notaPendienteEliminar = it },
                                            paddingContenido = PaddingValues(bottom = 8.dp),
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                    }
                                }

                                PestanaHomePaciente.TAREAS -> {
                                    if (uiState.tareas.isEmpty()) {
                                        TextoEstadoVacioHomePaciente(
                                            texto = "No tienes tareas asignadas.",
                                        )
                                    } else {
                                        ListaTareasPacienteApp(
                                            tareas = uiState.tareas,
                                            alPulsar = { t -> tareaIdDialogo = t.id },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (psicologoIdAsignado != null && pestanaActual == PestanaHomePaciente.NOTAS) {
            FloatingActionButton(
                onClick = alIrAAnadirNota,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Añadir nota")
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
private fun PestanasEstiloProto(
    pestanaActual: PestanaHomePaciente,
    alCambiarPestana: (PestanaHomePaciente) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                PestañaTextoSubrayada(
                    texto = "Mis notas",
                    seleccionada = pestanaActual == PestanaHomePaciente.NOTAS,
                    onClick = { alCambiarPestana(PestanaHomePaciente.NOTAS) },
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                PestañaTextoSubrayada(
                    texto = "Mis tareas",
                    seleccionada = pestanaActual == PestanaHomePaciente.TAREAS,
                    onClick = { alCambiarPestana(PestanaHomePaciente.TAREAS) },
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
    }
}

@Composable
private fun PestañaTextoSubrayada(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Medium,
            color = if (seleccionada) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(
                    if (seleccionada) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                ),
        )
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

@Composable
private fun TextoEstadoVacioHomePaciente(texto: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
        )
    }
}

