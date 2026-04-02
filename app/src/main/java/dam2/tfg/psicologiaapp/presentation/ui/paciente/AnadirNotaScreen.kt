package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.BarraSuperiorApp
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAnadirNota(
    alNotaGuardada: () -> Unit,
    alVolver: () -> Unit,
    viewModel: AnadirNotaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionAnadirNota.NotaGuardada -> {
                viewModel.alConsumirEventoNavegacion()
                alNotaGuardada()
            }
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            BarraSuperiorApp(
                titulo = "Añadir nota",
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
            CampoTextoApp(
                valor = uiState.asunto,
                alCambiar = viewModel::alCambiarAsunto,
                etiqueta = "Asunto",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            CampoTextoApp(
                valor = uiState.descripcion,
                alCambiar = viewModel::alCambiarDescripcion,
                etiqueta = "Descripción",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 4,
            )

            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = viewModel::guardarNota,
                enabled = uiState.esFormularioValido && !uiState.cargando,
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.cargando) "Guardando..." else "Guardar nota")
            }
        }
    }
}

