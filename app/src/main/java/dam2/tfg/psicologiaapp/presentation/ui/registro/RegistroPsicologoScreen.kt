package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.CampoContrasenaApp
import dam2.tfg.psicologiaapp.presentation.components.CampoCorreoApp
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.BotonTextoApp
import dam2.tfg.psicologiaapp.presentation.components.EstiloBotonTextoApp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity

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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val alturaCabecera = maxHeight * 0.12f
        val alturaOnda = 56.dp
        val colorSuperficie = MaterialTheme.colorScheme.surface

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

                drawPath(path = ruta, color = colorSuperficie)
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
                var tamanoCabecera by remember { mutableIntStateOf(0) }
                val densidad = LocalDensity.current
                val alturaCabeceraFija = with(densidad) { tamanoCabecera.toDp() }

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorSuperficie)
                            .onSizeChanged { tamanoCabecera = it.height }
                            .padding(horizontal = 22.dp, vertical = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        BotonTextoApp(
                            texto = "Volver",
                            alPulsar = alVolver,
                            estilo = EstiloBotonTextoApp.Enlace,
                            habilitado = !uiState.cargando,
                        )

                        Text(
                            text = "Registro psicólogo",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = alturaCabeceraFija)
                            .imePadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item { Spacer(modifier = Modifier.height(6.dp)) }

                        item {
                            CampoCorreoApp(
                                valor = uiState.correo,
                                alCambiar = viewModel::alCambiarCorreo,
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                            )
                        }

                        item {
                            CampoContrasenaApp(
                                valor = uiState.contrasena,
                                alCambiar = viewModel::alCambiarContrasena,
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                            )
                        }

                        item {
                            CampoTextoApp(
                                valor = uiState.nombre,
                                alCambiar = viewModel::alCambiarNombre,
                                etiqueta = "Nombre",
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                            )
                        }

                        item {
                            CampoTextoApp(
                                valor = uiState.apellidos,
                                alCambiar = viewModel::alCambiarApellidos,
                                etiqueta = "Apellidos",
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                            )
                        }

                        item {
                            CampoTextoApp(
                                valor = uiState.numeroColegiado,
                                alCambiar = viewModel::alCambiarNumeroColegiado,
                                etiqueta = "Número de colegiado",
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                            )
                        }

                        item {
                            CampoTextoApp(
                                valor = uiState.especialidad,
                                alCambiar = viewModel::alCambiarEspecialidad,
                                etiqueta = "Especialidad",
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                            )
                        }

                        item {
                            CampoTextoApp(
                                valor = uiState.descripcion,
                                alCambiar = viewModel::alCambiarDescripcion,
                                etiqueta = "Descripción (opcional)",
                                modifier = Modifier.fillMaxWidth(),
                                habilitado = !uiState.cargando,
                                singleLine = false,
                                minLines = 3,
                            )
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
                                alPulsar = viewModel::registrarPsicologo,
                                habilitado = !uiState.cargando,
                                cargando = uiState.cargando,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

