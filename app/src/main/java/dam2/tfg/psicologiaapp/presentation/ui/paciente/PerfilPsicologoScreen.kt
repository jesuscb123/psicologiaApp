package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import dam2.tfg.psicologiaapp.presentation.components.AvatarInicialApp
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilPsicologo(
    psicologoId: String,
    alAsignacionCompletada: () -> Unit,
    alVolver: () -> Unit,
    viewModel: PerfilPsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(psicologoId) {
        viewModel.cargar(psicologoId)
    }

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionPerfilPsicologo.AsignacionCompletada -> {
                viewModel.alConsumirEventoNavegacion()
                alAsignacionCompletada()
            }
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            BarraSuperiorApp(
                titulo = "Perfil psicólogo",
                alVolver = alVolver
            )
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

            val psicologo = uiState.psicologo
            if (psicologo == null) {
                Text("No se encontró el psicólogo")
                return@Column
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarInicialApp(nombre = psicologo.nombreUsuario, tamano = 56.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = psicologo.nombreUsuario,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = psicologo.especialidad,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Nº colegiado: ${psicologo.numeroColegiado}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::asignarPsicologo,
                enabled = !uiState.asignando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.asignando) "Asignando..." else "Asignar psicólogo")
            }
        }
    }
}

