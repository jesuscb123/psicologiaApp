package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.presentation.components.AccionesBarraMenuPerfilPaciente
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaPacienteApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomePsicologo(
    alIrAFichaPaciente: (Long) -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: HomePsicologoViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.recargar()
    }

    val uiState by viewModel.uiState.collectAsState()
    val tituloBarra = uiState.nombreUsuarioPsicologo.ifBlank { nombreUsuarioBarra }.ifBlank {
        "Psicólogo"
    }

    Scaffold(
        topBar = {
            BarraSuperiorApp(
                titulo = tituloBarra,
                subtitulo = "Pacientes",
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (uiState.cargando) {
                Text("Cargando...")
                return@Column
            }

            if (uiState.listaPacientes.isEmpty()) {
                Text(
                    text = "No tienes pacientes asignados",
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text(
                    text = "Tus pacientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                GridPacientes(
                    pacientes = uiState.listaPacientes,
                    alPulsarPaciente = { paciente ->
                        alIrAFichaPaciente(paciente.idPaciente)
                    },
                )
            }
        }
    }
}

@Composable
private fun GridPacientes(
    pacientes: List<Paciente>,
    alPulsarPaciente: (Paciente) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(pacientes) { paciente ->
            TarjetaPacienteApp(
                paciente = paciente,
                alPulsar = alPulsarPaciente,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
