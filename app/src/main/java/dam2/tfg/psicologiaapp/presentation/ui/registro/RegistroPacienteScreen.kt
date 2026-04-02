package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dam2.tfg.psicologiaapp.presentation.components.CampoContrasenaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoCorreoApp
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroPaciente(
    alRegistroCompletado: () -> Unit,
    alVolver: () -> Unit,
    viewModel: RegistroPacienteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registroCompletado) {
        if (uiState.registroCompletado) {
            viewModel.alConsumirRegistroCompletado()
            alRegistroCompletado()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperiorApp(
                titulo = "Registro paciente",
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
            CampoCorreoApp(
                valor = uiState.correo,
                alCambiar = viewModel::alCambiarCorreo,
                modifier = Modifier.fillMaxWidth(),
                habilitado = !uiState.cargando,
            )

            CampoContrasenaApp(
                valor = uiState.contrasena,
                alCambiar = viewModel::alCambiarContrasena,
                modifier = Modifier.fillMaxWidth(),
                habilitado = !uiState.cargando,
            )

            CampoTextoApp(
                valor = uiState.nombreUsuario,
                alCambiar = viewModel::alCambiarNombreUsuario,
                etiqueta = "Nombre de usuario",
                modifier = Modifier.fillMaxWidth(),
                habilitado = !uiState.cargando,
            )

            uiState.mensajeError?.let { Text(it) }

            Button(
                onClick = viewModel::registrarPaciente,
                enabled = !uiState.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.cargando) "Registrando..." else "Completar registro")
            }
        }
    }
}

