package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

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
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.BotonFlotantePrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.EstadoVacioContenidoApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

@Composable
fun NotasPacienteScreen(
    alVolver: () -> Unit,
    alIrAAnadirNota: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: HomePacienteViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.sincronizarSiProcede()
    }

    val uiState by viewModel.uiState.collectAsState()
    var notaPendienteEliminar by remember { mutableStateOf<Nota?>(null) }

    notaPendienteEliminar?.let { nota ->
        AlertDialog(
            onDismissRequest = { notaPendienteEliminar = null },
            title = { Text("Eliminar nota") },
            text = { Text("Esta accion no se puede deshacer. ¿Quieres continuar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarNota(nota.id)
                        notaPendienteEliminar = null
                    },
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { notaPendienteEliminar = null }) { Text("Cancelar") }
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PantallaConCabeceraOndaApp(
            encabezado = {
                EncabezadoUsuarioApp(
                    tituloCentro = "Mis notas",
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

                        uiState.notas.isEmpty() -> {
                            EstadoVacioContenidoApp(
                                titulo = "Tu diario está en blanco",
                                subtitulo = "Pulsa el botón + para registrar cómo te sientes y compartirlo con tu psicólogo.",
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

                        else -> {
                            ListaNotasApp(
                                notas = uiState.notas,
                                modifier = Modifier.fillMaxSize(),
                                paddingContenido = PaddingValues(bottom = 88.dp),
                                permitirEliminar = true,
                                alSolicitarEliminar = { notaPendienteEliminar = it },
                            )
                        }
                    }
                }
            }
        }

        if (!uiState.cargando && uiState.perfilPaciente?.psicologoId != null) {
            BotonFlotantePrimarioApp(
                alPulsar = alIrAAnadirNota,
                descripcionIcono = "Anadir nota",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }
}
