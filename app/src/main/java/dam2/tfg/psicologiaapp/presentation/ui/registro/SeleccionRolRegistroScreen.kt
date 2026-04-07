package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.BotonSecundarioApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSeleccionRolRegistro(
    alElegirPaciente: () -> Unit,
    alElegirPsicologo: () -> Unit,
    alVolver: () -> Unit,
    viewModel: SeleccionRolRegistroViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionSeleccionRol.IrARegistroPaciente -> {
                viewModel.alConsumirEventoNavegacion()
                alElegirPaciente()
            }
            EventoNavegacionSeleccionRol.IrARegistroPsicologo -> {
                viewModel.alConsumirEventoNavegacion()
                alElegirPsicologo()
            }
            EventoNavegacionSeleccionRol.Volver -> {
                viewModel.alConsumirEventoNavegacion()
                alVolver()
            }
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            BarraSuperiorApp(
                titulo = "Registro",
                alVolver = viewModel::volver
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
            TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Elige el rol para continuar",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    BotonPrimarioApp(
                        texto = "Paciente",
                        alPulsar = viewModel::elegirPaciente,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    BotonSecundarioApp(
                        texto = "Psicólogo",
                        alPulsar = viewModel::elegirPsicologo,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

