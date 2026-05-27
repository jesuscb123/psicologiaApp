package dam2.tfg.psicologiaapp.presentation.ui.registro.psicologo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dam2.tfg.psicologiaapp.presentation.components.EditorEspecialidadesApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.EstiloCamposRegistro
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.LimitesCaracteresRegistro

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
        colorCabecera = MaterialTheme.colorScheme.background,
        usarGradienteCabecera = false,
        paddingEncabezado = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        alineacionEncabezado = Alignment.TopStart,
        proporcionAlturaCabecera = 0.10f,
        alturaOnda = 56.dp,
        paddingContenido = PaddingValues(horizontal = 22.dp, vertical = 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Registro de\nEspecialista",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Únete a nuestra red de profesionales y expande tu impacto.",
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
                CampoConEtiquetaExternaApp(
                    etiqueta = "Número de colegiado",
                    etiquetaEnMayusculas = true,
                ) {
                    CampoTextoApp(
                        valor = uiState.numeroColegiado,
                        alCambiar = viewModel::alCambiarNumeroColegiado,
                        etiqueta = "",
                        placeholder = "Ej. 12345",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                        textoError = uiState.errorLongitudNumeroColegiado,
                        paddingExterno = EstiloCamposRegistro.paddingCampo,
                        estilo = EstiloCamposRegistro.estiloCampo,
                    )
                }
            }

            item {
                CampoConEtiquetaExternaApp(
                    etiqueta = "Descripción (opcional)",
                    etiquetaEnMayusculas = true,
                ) {
                    CampoTextoApp(
                        valor = uiState.descripcion,
                        alCambiar = viewModel::alCambiarDescripcion,
                        etiqueta = "",
                        placeholder = "Cuéntale a tus pacientes tu enfoque…",
                        modifier = Modifier.fillMaxWidth(),
                        habilitado = !uiState.cargando,
                        singleLine = false,
                        minLines = 3,
                        textoError = uiState.errorLongitudDescripcion,
                        paddingExterno = EstiloCamposRegistro.paddingCampo,
                        estilo = EstiloCamposRegistro.estiloCampo,
                    )
                }
            }

            item {
                CampoConEtiquetaExternaApp(
                    etiqueta = "Especialidades",
                    etiquetaEnMayusculas = true,
                    textoAuxiliarDerecha = "${uiState.especialidades.size}/${LimitesCaracteresRegistro.Psicologo.MAX_ESPECIALIDADES}",
                ) {
                    EditorEspecialidadesApp(
                        especialidades = uiState.especialidades,
                        especialidadInput = uiState.especialidadInput,
                        errorEspecialidadInput = uiState.errorEspecialidadInput,
                        habilitado = !uiState.cargando,
                        maxEspecialidades = LimitesCaracteresRegistro.Psicologo.MAX_ESPECIALIDADES,
                        alCambiarInput = viewModel::alCambiarEspecialidadInput,
                        alAnadir = viewModel::alAnadirEspecialidad,
                        alEliminar = viewModel::alEliminarEspecialidad,
                        formaEntrada = EstiloCamposRegistro.formaEditorEspecialidades,
                        modifier = Modifier.fillMaxWidth(),
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
