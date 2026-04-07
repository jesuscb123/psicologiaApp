package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.AccionesBarraMenuPerfilPaciente
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFichaPacientePsicologo(
    alVolver: () -> Unit,
    alIrAnadirTarea: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: FichaPacientePsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.recargar()
    }

    val tituloPaciente = uiState.nombreUsuarioPaciente.ifBlank { "Paciente" }

    Scaffold(
        topBar = {
            BarraSuperiorApp(
                titulo = tituloPaciente,
                subtitulo = "Ficha",
                mostrarAvatarJuntoTitulo = true,
                fotoPerfilUrlAvatarTitulo = uiState.fotoPerfilUrlPaciente,
                alVolver = alVolver,
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
            if (uiState.pestanaActual == PestanaFichaPacientePsi.TAREAS) {
                FloatingActionButton(onClick = alIrAnadirTarea) {
                    Text("+")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (uiState.cargando) {
                Text("Cargando...")
                return@Column
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = uiState.pestanaActual == PestanaFichaPacientePsi.NOTAS,
                    onClick = { viewModel.cambiarPestana(PestanaFichaPacientePsi.NOTAS) },
                    label = { Text("Notas") },
                )
                FilterChip(
                    selected = uiState.pestanaActual == PestanaFichaPacientePsi.TAREAS,
                    onClick = { viewModel.cambiarPestana(PestanaFichaPacientePsi.TAREAS) },
                    label = { Text("Tareas") },
                )
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
                                paddingContenido = PaddingValues(bottom = 12.dp),
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
    }
}
