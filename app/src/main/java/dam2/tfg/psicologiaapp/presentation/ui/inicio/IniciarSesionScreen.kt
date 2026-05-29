package dam2.tfg.psicologiaapp.presentation.ui.inicio

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.R
import dam2.tfg.psicologiaapp.presentation.components.BotonTextoApp
import dam2.tfg.psicologiaapp.presentation.components.CampoContrasenaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoCorreoApp
import dam2.tfg.psicologiaapp.presentation.components.EstiloBotonTextoApp
import dam2.tfg.psicologiaapp.presentation.components.EstiloCampoTextoApp
import dam2.tfg.psicologiaapp.ui.theme.brushGradienteFirma

@Composable
fun PantallaIniciarSesion(
    alPulsarCrearCuenta: () -> Unit,
    alEntrarComoPaciente: () -> Unit,
    alEntrarComoPsicologo: () -> Unit,
    viewModel: IniciarSesionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val contexto = LocalContext.current

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

    LaunchedEffect(uiState.mensajeInfoRecuperacion) {
        uiState.mensajeInfoRecuperacion?.let { mensaje ->
            Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show()
            viewModel.alConsumirMensajeInfoRecuperacion()
        }
    }

    val paddingCampos = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    val colorFondo = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .widthIn(max = 400.dp)
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
        
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = "Logo de la aplicación",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(5.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Acompañándote durante todo tu proceso.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                CampoCorreoApp(
                    valor = uiState.correo,
                    alCambiar = viewModel::alCambiarCorreo,
                    modifier = Modifier.fillMaxWidth(),
                    etiqueta = "Correo electrónico",
                    placeholder = "tu@email.com",
                    habilitado = !uiState.cargando,
                    paddingExterno = paddingCampos,
                    estilo = EstiloCampoTextoApp.ContenedorAlta,
                    iconoInicio = Icons.Filled.Email,
                    contenidoDescripcionIconoInicio = null,
                )

                CampoContrasenaApp(
                    valor = uiState.contrasena,
                    alCambiar = viewModel::alCambiarContrasena,
                    modifier = Modifier.fillMaxWidth(),
                    etiqueta = "Contraseña",
                    placeholder = "••••••••",
                    habilitado = !uiState.cargando,
                    paddingExterno = paddingCampos,
                    estilo = EstiloCampoTextoApp.ContenedorAlta,
                    iconoInicio = Icons.Filled.Lock,
                    contenidoDescripcionIconoInicio = null,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BotonTextoApp( 
                        texto = "¿Olvidaste tu contraseña?",
                        alPulsar = viewModel::abrirDialogoRecuperacion,
                        habilitado = !uiState.cargando && !uiState.cargandoRecuperacion,
                        estilo = EstiloBotonTextoApp.Enlace,
                    )
                }

                uiState.mensajeError?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                val formaCta = MaterialTheme.shapes.extraLarge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(formaCta)
                        .background(brushGradienteFirma())
                        .clickable(enabled = !uiState.cargando) { viewModel.iniciarSesion() }
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (uiState.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Iniciar sesión",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "¿No tienes una cuenta?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                BotonTextoApp(
                    texto = "Regístrate",
                    alPulsar = alPulsarCrearCuenta,
                    habilitado = !uiState.cargando,
                    estilo = EstiloBotonTextoApp.Enlace,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (uiState.mostrandoDialogoRecuperacion) {
            AlertDialog(
                onDismissRequest = {
                    if (!uiState.cargandoRecuperacion) {
                        viewModel.cerrarDialogoRecuperacion()
                    }
                },
                title = { Text("Recuperar contraseña") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Introduce tu correo electrónico para recibir instrucciones de restablecimiento."
                        )
                        CampoCorreoApp(
                            valor = uiState.correoRecuperacion,
                            alCambiar = viewModel::alCambiarCorreoRecuperacion,
                            modifier = Modifier.fillMaxWidth(),
                            etiqueta = "Correo electrónico",
                            placeholder = "tu@email.com",
                            habilitado = !uiState.cargandoRecuperacion,
                            paddingExterno = paddingCampos,
                            estilo = EstiloCampoTextoApp.ContenedorAlta,
                            iconoInicio = Icons.Filled.Email,
                            contenidoDescripcionIconoInicio = null,
                        )
                        uiState.mensajeErrorRecuperacion?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = viewModel::solicitarRecuperacionContrasena,
                        enabled = !uiState.cargandoRecuperacion,
                    ) {
                        if (uiState.cargandoRecuperacion) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 8.dp),
                                strokeWidth = 2.dp,
                            )
                        }
                        Text("Enviar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = viewModel::cerrarDialogoRecuperacion,
                        enabled = !uiState.cargandoRecuperacion,
                    ) {
                        Text("Cancelar")
                    }
                },
            )
        }
    }
}
