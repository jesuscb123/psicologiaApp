package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.EstadoVacioContenidoApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasPacienteApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

private enum class FiltroTareasPaciente {
    PENDIENTES,
    COMPLETADAS,
}

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
    var filtroSeleccionadoNombre by rememberSaveable {
        mutableStateOf(FiltroTareasPaciente.PENDIENTES.name)
    }
    val filtroSeleccionado = FiltroTareasPaciente.valueOf(filtroSeleccionadoNombre)

    val tareasFiltradas = remember(uiState.tareas, filtroSeleccionado) {
        when (filtroSeleccionado) {
            FiltroTareasPaciente.PENDIENTES -> uiState.tareas.filter { !it.realizada }
            FiltroTareasPaciente.COMPLETADAS -> uiState.tareas.filter { it.realizada }
        }
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

            if (
                !uiState.cargando &&
                uiState.perfilPaciente?.psicologoId != null &&
                uiState.tareas.isNotEmpty()
            ) {
                val contadorPendientes = uiState.tareas.count { !it.realizada }
                val contadorCompletadas = uiState.tareas.count { it.realizada }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selected = filtroSeleccionado == FiltroTareasPaciente.PENDIENTES,
                        onClick = { filtroSeleccionadoNombre = FiltroTareasPaciente.PENDIENTES.name },
                        label = {
                            Text(
                                text = "Pendientes ($contadorPendientes)",
                                fontWeight = if (filtroSeleccionado == FiltroTareasPaciente.PENDIENTES) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                    FilterChip(
                        selected = filtroSeleccionado == FiltroTareasPaciente.COMPLETADAS,
                        onClick = { filtroSeleccionadoNombre = FiltroTareasPaciente.COMPLETADAS.name },
                        label = {
                            Text(
                                text = "Completadas ($contadorCompletadas)",
                                fontWeight = if (filtroSeleccionado == FiltroTareasPaciente.COMPLETADAS) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
            }

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
                        EstadoVacioContenidoApp(
                            titulo = "Aún no tienes tareas",
                            subtitulo = "Cuando tu psicólogo te asigne actividades, aparecerán aquí para que las completes.",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    tareasFiltradas.isEmpty() -> {
                        val mensaje = when (filtroSeleccionado) {
                            FiltroTareasPaciente.PENDIENTES -> "No tienes tareas pendientes."
                            FiltroTareasPaciente.COMPLETADAS -> "No tienes tareas completadas."
                        }
                        Text(
                            text = mensaje,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    else -> {
                        ListaTareasPacienteApp(
                            tareas = tareasFiltradas,
                            alAceptarTarea = { tareaId -> viewModel.aceptarTarea(tareaId) },
                            alCompletarTarea = { tareaId ->
                                viewModel.marcarTareaRealizada(tareaId, realizada = true)
                            },
                            modifier = Modifier.fillMaxSize(),
                            paddingContenido = PaddingValues(bottom = 24.dp),
                        )
                    }
                }
            }
        }
    }
}
