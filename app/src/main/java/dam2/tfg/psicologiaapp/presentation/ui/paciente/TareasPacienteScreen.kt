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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasPacienteApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

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

    LaunchedEffect(Unit) { viewModel.recargar() }

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
                            alAceptarTarea = { tareaId -> viewModel.aceptarTarea(tareaId) },
                            alCompletarTarea = { tareaId -> viewModel.marcarTareaRealizada(tareaId, realizada = true) },
                            modifier = Modifier.fillMaxSize(),
                            paddingContenido = PaddingValues(bottom = 24.dp),
                        )
                    }
                }
            }
        }
    }
}
