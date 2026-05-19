package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.CampoConEtiquetaExternaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoContrasenaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoCorreoApp
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

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

    PantallaConCabeceraOndaApp(
        encabezado = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = alVolver,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    enabled = !uiState.cargando,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        colorCabecera = MaterialTheme.colorScheme.background,
        paddingEncabezado = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        alineacionEncabezado = Alignment.TopStart,
        proporcionAlturaCabecera = 0.10f,
        alturaOnda = 56.dp,
        paddingContenido = PaddingValues(horizontal = 22.dp, vertical = 16.dp),
        contenido = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Registro de\nPaciente",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Crea tu cuenta para comenzar tu proceso con tranquilidad.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                item {
                    CampoConEtiquetaExternaApp(
                        etiqueta = "Correo electrónico",
                        etiquetaEnMayusculas = true,
                    ) {
                        CampoCorreoApp(
                            valor = uiState.correo,
                            alCambiar = viewModel::alCambiarCorreo,
                            etiqueta = "",
                            placeholder = "tu@email.com",
                            modifier = Modifier.fillMaxWidth(),
                            habilitado = !uiState.cargando,
                            textoError = uiState.errorLongitudCorreo,
                            paddingExterno = EstiloCamposRegistro.paddingCampo,
                            estilo = EstiloCamposRegistro.estiloCampo,
                        )
                    }
                }

                item {
                    CampoConEtiquetaExternaApp(
                        etiqueta = "Contraseña",
                        etiquetaEnMayusculas = true,
                    ) {
                        CampoContrasenaApp(
                            valor = uiState.contrasena,
                            alCambiar = viewModel::alCambiarContrasena,
                            etiqueta = "",
                            placeholder = "••••••••",
                            modifier = Modifier.fillMaxWidth(),
                            habilitado = !uiState.cargando,
                            paddingExterno = EstiloCamposRegistro.paddingCampo,
                            estilo = EstiloCamposRegistro.estiloCampo,
                        )
                    }
                }

                item {
                    CampoConEtiquetaExternaApp(
                        etiqueta = "Nombre",
                        etiquetaEnMayusculas = true,
                    ) {
                        CampoTextoApp(
                            valor = uiState.nombre,
                            alCambiar = viewModel::alCambiarNombre,
                            etiqueta = "",
                            placeholder = "Ej. Juan",
                            modifier = Modifier.fillMaxWidth(),
                            habilitado = !uiState.cargando,
                            textoError = uiState.errorLongitudNombre,
                            paddingExterno = EstiloCamposRegistro.paddingCampo,
                            estilo = EstiloCamposRegistro.estiloCampo,
                        )
                    }
                }

                item {
                    CampoConEtiquetaExternaApp(
                        etiqueta = "Apellidos",
                        etiquetaEnMayusculas = true,
                    ) {
                        CampoTextoApp(
                            valor = uiState.apellidos,
                            alCambiar = viewModel::alCambiarApellidos,
                            etiqueta = "",
                            placeholder = "Ej. Pérez",
                            modifier = Modifier.fillMaxWidth(),
                            habilitado = !uiState.cargando,
                            textoError = uiState.errorLongitudApellidos,
                            paddingExterno = EstiloCamposRegistro.paddingCampo,
                            estilo = EstiloCamposRegistro.estiloCampo,
                        )
                    }
                }

                item {
                    uiState.mensajeError?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                item {
                    BotonPrimarioApp(
                        texto = if (uiState.cargando) "Registrando..." else "Completar registro",
                        alPulsar = viewModel::registrarPaciente,
                        habilitado = !uiState.cargando,
                        cargando = uiState.cargando,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        },
    )
}

