package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.PestanasPildoraDosOpcionesApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFichaPacientePsicologo(
    alVolver: () -> Unit,
    alIrAnadirTarea: () -> Unit,
    alIrAChat: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: FichaPacientePsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var notaSeleccionada by remember { mutableStateOf<Nota?>(null) }

    LaunchedEffect(Unit) {
        viewModel.recargar()
    }

    notaSeleccionada?.let { nota ->
        AlertDialog(
            onDismissRequest = { notaSeleccionada = null },
            title = {
                Text(
                    text = nota.asunto,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = nota.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { notaSeleccionada = null }) { Text("Cerrar") }
            },
        )
    }

    val tituloPaciente = uiState.nombreUsuarioPaciente.ifBlank { "Paciente" }

    Box(modifier = Modifier.fillMaxSize()) {
        PantallaConCabeceraOndaApp(
            encabezado = { },
            cabecera = {
                EncabezadoUsuarioApp(
                    tituloCentro = tituloPaciente,
                    mostrarFlechaAtras = true,
                    alVolver = alVolver,
                    nombreUsuario = nombreUsuarioBarra,
                    fotoPerfilUrl = fotoPerfilUrlBarra,
                    revisionCacheFoto = revisionCacheFotoBarra,
                    alAbrirMenuPerfil = alAbrirMenuPerfil,
                )
            },
            contenido = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    uiState.mensajeError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    if (uiState.cargando) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        return@Column
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        PestanasPildoraDosOpcionesApp(
                            primeraEtiqueta = "Notas",
                            segundaEtiqueta = "Tareas",
                            indiceSeleccionado = when (uiState.pestanaActual) {
                                PestanaFichaPacientePsi.NOTAS -> 0
                                PestanaFichaPacientePsi.TAREAS -> 1
                            },
                            alSeleccionarPrimera = { viewModel.cambiarPestana(PestanaFichaPacientePsi.NOTAS) },
                            alSeleccionarSegunda = { viewModel.cambiarPestana(PestanaFichaPacientePsi.TAREAS) },
                            modifier = Modifier.weight(1f),
                        )
                        FilledTonalButton(
                            onClick = alIrAChat,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(text = "Chat")
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (uiState.pestanaActual) {
                            PestanaFichaPacientePsi.NOTAS -> {
                                if (uiState.notas.isEmpty()) {
                                    Text(
                                        text = "No hay notas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                } else {
                                    ListaNotasApp(
                                        notas = uiState.notas,
                                        permitirEliminar = false,
                                        paddingContenido = PaddingValues(bottom = 88.dp),
                                        alVerDetalle = { notaSeleccionada = it },
                                    )
                                }
                            }

                            PestanaFichaPacientePsi.TAREAS -> {
                                if (uiState.tareas.isEmpty()) {
                                    Text(
                                        text = "No hay tareas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                } else {
                                    ListaTareasApp(
                                        tareas = uiState.tareas,
                                        paddingContenido = PaddingValues(bottom = 12.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )

        if (uiState.pestanaActual == PestanaFichaPacientePsi.TAREAS && !uiState.cargando) {
            FloatingActionButton(
                onClick = alIrAnadirTarea,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir tarea")
            }
        }
    }
}
