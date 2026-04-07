package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.AccionesBarraMenuPerfilPaciente
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasPacienteApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaPsicologoApp
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

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

    Scaffold(
        topBar = {
            val titulo = uiState.psicologoAsignado?.let { "Tu psicólogo" } ?: "Home paciente"
            BarraSuperiorApp(
                titulo = titulo,
                subtitulo = uiState.psicologoAsignado?.nombreUsuario,
                acciones = {
                    AccionesBarraMenuPerfilPaciente(
                        nombreUsuario = nombreUsuarioBarra,
                        fotoPerfilUrl = fotoPerfilUrlBarra,
                        revisionCacheFoto = revisionCacheFotoBarra,
                        alAbrirMenu = alAbrirMenuPerfil,
                    )
                },
            )
        },
        floatingActionButton = {
            if (uiState.perfilPaciente?.psicologoId != null) {
                FloatingActionButton(
                    onClick = alIrAAnadirNota,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Añadir nota")
                }
            }
        }
    ) { padding ->
        val psicologoIdAsignado = uiState.perfilPaciente?.psicologoId
        val paddingContenido = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 72.dp)

        when {
            uiState.cargando -> {
                Column(
                    modifier = paddingContenido,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Text("Cargando...")
                }
            }

            psicologoIdAsignado == null -> {
                // Sin scroll vertical: LazyVerticalGrid no puede ir dentro de Column(verticalScroll).
                Column(
                    modifier = paddingContenido,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Elige tu psicólogo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            GridPsicologos(
                                psicologos = uiState.listaPsicologos,
                                alPulsarPsicologo = { psicologo ->
                                    alIrAPerfilPsicologo(psicologo.usuarioId.toString())
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = paddingContenido.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Tus notas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

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
                    }

                    Spacer(Modifier.height(8.dp))

                    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Tus tareas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

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

@Composable
private fun GridPsicologos(
    psicologos: List<Psicologo>,
    alPulsarPsicologo: (Psicologo) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
        modifier = Modifier.fillMaxWidth()
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

