package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasPacienteApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

@Composable
fun TareasPacienteScreen(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: HomePacienteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }

    LaunchedEffect(Unit) { viewModel.recargar() }

    tareaSeleccionada?.let { tarea ->
        val tareaPendienteAceptar = !tarea.aceptadaPorPaciente && !tarea.realizada
        val tareaPendienteCompletar = tarea.aceptadaPorPaciente && !tarea.realizada
        val tituloDialogo = when {
            tareaPendienteAceptar -> "Aceptar tarea"
            tareaPendienteCompletar -> "Marcar tarea como completada"
            else -> "Tarea completada"
        }
        val descripcionAccion = when {
            tareaPendienteAceptar -> "¿Quieres aceptar esta tarea?"
            tareaPendienteCompletar -> "¿Quieres marcar esta tarea como completada?"
            else -> "Esta tarea ya esta completada."
        }

        AlertDialog(
            onDismissRequest = { tareaSeleccionada = null },
            title = { Text(tituloDialogo) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (tarea.descripcion.isNotBlank()) {
                        Text(
                            text = tarea.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Text(
                        text = descripcionAccion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            confirmButton = {
                if (tareaPendienteAceptar || tareaPendienteCompletar) {
                    TextButton(
                        onClick = {
                            if (tareaPendienteAceptar) {
                                viewModel.aceptarTarea(tarea.id)
                            } else {
                                viewModel.marcarTareaRealizada(tarea.id, realizada = true)
                            }
                            tareaSeleccionada = null
                        },
                    ) {
                        Text(if (tareaPendienteAceptar) "Aceptar" else "Completar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaSeleccionada = null }) {
                    Text(if (tareaPendienteAceptar || tareaPendienteCompletar) "Cancelar" else "Cerrar")
                }
            },
        )
    }

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Mis tareas",
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
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                when {
                    uiState.cargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    uiState.perfilPaciente?.psicologoId == null -> {
                        Text(
                            text = "Aun no tienes psicologo asignado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.TopStart),
                        )
                    }

                    uiState.tareas.isEmpty() -> {
                        Text(
                            text = "No hay tareas todavia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.TopStart),
                        )
                    }

                    else -> {
                        ListaTareasPacienteApp(
                            tareas = uiState.tareas,
                            alPulsar = { tareaSeleccionada = it },
                            modifier = Modifier.fillMaxSize(),
                            paddingContenido = PaddingValues(bottom = 24.dp),
                        )
                    }
                }
            }
        }
    }
}
