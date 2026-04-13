package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasPacienteApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaPsicologoApp
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

private enum class PestanaHomePaciente {
    NOTAS,
    TAREAS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomePaciente(
    alIrAPerfilPsicologo: (String) -> Unit,
    alIrAAnadirNota: () -> Unit,
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
                    mostrarFlechaAtras = true,
                    alVolver = null,
                    nombreUsuario = nombreUsuarioBarra,
                    fotoPerfilUrl = fotoPerfilUrlBarra,
                    revisionCacheFoto = revisionCacheFotoBarra,
                    alAbrirMenuPerfil = alAbrirMenuPerfil,
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                uiState.cargando -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        Text("Cargando...")
                    }
                }

                psicologoIdAsignado == null -> {
                    // Sin scroll vertical: LazyVerticalGrid no puede ir dentro de Column(verticalScroll).
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        TarjetaApp(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    text = "Elige tu psicólogo",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                GridPsicologos(
                                    psicologos = uiState.listaPsicologos,
                                    alPulsarPsicologo = { psicologo ->
                                        alIrAPerfilPsicologo(psicologo.usuarioId.toString())
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                )
                            }
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(bottom = 84.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = pestanaActual == PestanaHomePaciente.NOTAS,
                                onClick = { pestanaActual = PestanaHomePaciente.NOTAS },
                                label = { Text("Notas") },
                            )
                            FilterChip(
                                selected = pestanaActual == PestanaHomePaciente.TAREAS,
                                onClick = { pestanaActual = PestanaHomePaciente.TAREAS },
                                label = { Text("Tareas") },
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (pestanaActual) {
                                PestanaHomePaciente.NOTAS -> {
                                    if (uiState.notas.isEmpty()) {
                                        Text(
                                            text = "Todavía no hay notas existentes",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    } else {
                                        ListaNotasApp(
                                            notas = uiState.notas,
                                            alSolicitarEliminar = { notaPendienteEliminar = it },
                                            listaPlana = true,
                                            paddingContenido = PaddingValues(bottom = 8.dp),
                                        )
                                    }
                                }

                                PestanaHomePaciente.TAREAS -> {
                                    if (uiState.tareas.isEmpty()) {
                                        Text(
                                            text = "No tienes tareas asignadas",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.extraLarge,
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
private fun GridPsicologos(
    psicologos: List<Psicologo>,
    alPulsarPsicologo: (Psicologo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(psicologos) { psicologo ->
            TarjetaPsicologoApp(
                psicologo = psicologo,
                alPulsar = alPulsarPsicologo,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}

