package dam2.tfg.psicologiaapp.presentation.ui.inicio

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
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.BotonTextoApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIniciarSesion(
    alPulsarCrearCuenta: () -> Unit,
    alEntrarComoPaciente: () -> Unit,
    alEntrarComoPsicologo: () -> Unit,
    viewModel: IniciarSesionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionIniciarSesion.IrARegistro -> {
                viewModel.alConsumirEventoNavegacion()
                alPulsarCrearCuenta()
            }
            EventoNavegacionIniciarSesion.IrAHomePaciente -> {
                viewModel.alConsumirEventoNavegacion()
                alEntrarComoPaciente()
            }
            EventoNavegacionIniciarSesion.IrAHomePsicologo -> {
                viewModel.alConsumirEventoNavegacion()
                alEntrarComoPsicologo()
            }
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            BarraSuperiorApp(titulo = "Iniciar sesión")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaApp(
                modifier = Modifier.fillMaxWidth(),
            ) {
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

                    uiState.mensajeError?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    BotonPrimarioApp(
                        texto = if (uiState.cargando) "Entrando..." else "Entrar",
                        alPulsar = viewModel::iniciarSesion,
                        habilitado = !uiState.cargando,
                        cargando = uiState.cargando,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            BotonTextoApp(
                texto = "Crear una cuenta",
                alPulsar = alPulsarCrearCuenta,
                habilitado = !uiState.cargando,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

