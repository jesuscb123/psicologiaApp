package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.AccionesBarraMenuPerfilPaciente
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
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
                FloatingActionButton(onClick = alIrAAnadirNota) {
                    Text("+")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (uiState.cargando) {
                Text("Cargando...")
                return@Column
            }

            val psicologoIdAsignado = uiState.perfilPaciente?.psicologoId
            if (psicologoIdAsignado == null) {
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
            } else {
                Text(
                    text = "Tus notas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (uiState.notas.isEmpty()) {
                    Text("Todavía no hay notas existentes")
                } else {
                    ListaNotasApp(
                        notas = uiState.notas,
                        alSolicitarEliminar = { notaPendienteEliminar = it }
                    )
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

