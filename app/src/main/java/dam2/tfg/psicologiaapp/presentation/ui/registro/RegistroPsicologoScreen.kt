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
import dam2.tfg.psicologiaapp.presentation.components.CampoContrasenaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoCorreoApp
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroPsicologo(
    alRegistroCompletado: () -> Unit,
    alVolver: () -> Unit,
    viewModel: RegistroPsicologoViewModel = hiltViewModel(),
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
                titulo = "Registro psicólogo",
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
            TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        valor = uiState.nombre,
                        alCambiar = viewModel::alCambiarNombre,
                        etiqueta = "Nombre",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                    )

                    CampoTextoApp(
                        valor = uiState.apellidos,
                        alCambiar = viewModel::alCambiarApellidos,
                        etiqueta = "Apellidos",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                    )

                    CampoTextoApp(
                        valor = uiState.numeroColegiado,
                        alCambiar = viewModel::alCambiarNumeroColegiado,
                        etiqueta = "Número de colegiado",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                    )

                    CampoTextoApp(
                        valor = uiState.especialidad,
                        alCambiar = viewModel::alCambiarEspecialidad,
                        etiqueta = "Especialidad",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                    )

                    CampoTextoApp(
                        valor = uiState.descripcion,
                        alCambiar = viewModel::alCambiarDescripcion,
                        etiqueta = "Descripción (opcional)",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                        singleLine = false,
                        minLines = 3,
                    )

                    uiState.mensajeError?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    BotonPrimarioApp(
                        texto = if (uiState.cargando) "Registrando..." else "Completar registro",
                        alPulsar = viewModel::registrarPsicologo,
                        habilitado = !uiState.cargando,
                        cargando = uiState.cargando,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

