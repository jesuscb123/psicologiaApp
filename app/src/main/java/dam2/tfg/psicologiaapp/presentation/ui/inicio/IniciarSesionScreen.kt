package dam2.tfg.psicologiaapp.presentation.ui.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.R
import dam2.tfg.psicologiaapp.presentation.components.CampoContrasenaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoCorreoApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.BotonTextoApp
import dam2.tfg.psicologiaapp.presentation.components.EstiloBotonTextoApp
import dam2.tfg.psicologiaapp.presentation.components.EstiloCampoTextoApp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIniciarSesion(
    alPulsarCrearCuenta: () -> Unit,
    alEntrarComoPaciente: () -> Unit,
    alEntrarComoPsicologo: () -> Unit,
    viewModel: IniciarSesionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var recordarSesion by rememberSaveable { mutableStateOf(false) }

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

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val alturaCabecera = maxHeight * 0.12f
        val alturaOnda = 72.dp
        val colorSuperficie = MaterialTheme.colorScheme.surface
        val paddingCampos = PaddingValues(horizontal = 0.dp, vertical = 0.dp)

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaCabecera)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary,
                            ),
                        ),
                    ),
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaOnda)
                    .offset(y = alturaCabecera - 1.dp),
            ) {
                val ancho = size.width
                val alto = size.height
                val ruta = Path().apply {
                    moveTo(0f, 0f)
                    quadraticTo(
                        ancho * 0.25f,
                        alto * 0.85f,
                        ancho * 0.5f,
                        alto * 0.45f,
                    )
                    quadraticTo(
                        ancho * 0.75f,
                        0f,
                        ancho,
                        alto * 0.55f,
                    )
                    lineTo(ancho, alto)
                    lineTo(0f, alto)
                    close()
                }

                drawPath(
                    path = ruta,
                    color = colorSuperficie,
                )

                drawLine(
                    color = colorSuperficie,
                    start = Offset(0f, alto),
                    end = Offset(ancho, alto),
                    strokeWidth = 2f,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(y = alturaCabecera + alturaOnda - 24.dp),
                color = colorSuperficie,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logoapp),
                            contentDescription = "Logo de la aplicación",
                            modifier = Modifier.size(148.dp),
                        )

                        CampoCorreoApp(
                            valor = uiState.correo,
                            alCambiar = viewModel::alCambiarCorreo,
                            modifier = Modifier.fillMaxWidth(),
                            habilitado = !uiState.cargando,
                            paddingExterno = paddingCampos,
                            estilo = EstiloCampoTextoApp.Minimal,
                        )

                        CampoContrasenaApp(
                            valor = uiState.contrasena,
                            alCambiar = viewModel::alCambiarContrasena,
                            modifier = Modifier.fillMaxWidth(),
                            habilitado = !uiState.cargando,
                            paddingExterno = paddingCampos,
                            estilo = EstiloCampoTextoApp.Minimal,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable(enabled = !uiState.cargando) { recordarSesion = !recordarSesion },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = recordarSesion,
                                onCheckedChange = { if (!uiState.cargando) recordarSesion = it },
                                enabled = !uiState.cargando,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recordarme",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        BotonTextoApp(
                            texto = "¿Olvidaste la contraseña?",
                            alPulsar = {},
                            habilitado = !uiState.cargando,
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

                    BotonPrimarioApp(
                        texto = if (uiState.cargando) "Entrando..." else "Entrar",
                        alPulsar = viewModel::iniciarSesion,
                        habilitado = !uiState.cargando,
                        cargando = uiState.cargando,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "¿No tienes cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        BotonTextoApp(
                            texto = "Regístrate",
                            alPulsar = alPulsarCrearCuenta,
                            habilitado = !uiState.cargando,
                            estilo = EstiloBotonTextoApp.Enlace,
                        )
                    }
                }
            }
        }
    }
}

