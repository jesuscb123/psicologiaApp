package dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.AvatarPerfilCircularApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoBaseApp
import dam2.tfg.psicologiaapp.presentation.components.EditorEspecialidadesApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.LimitesCaracteresRegistro

@Composable
fun PantallaAjustesPsicologo(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    alPulsarCambiarFotoPerfil: () -> Unit = {},
    cargandoFotoPerfil: Boolean = false,
    mensajeErrorFotoPerfil: String? = null,
    viewModel: AjustesPsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Modificar perfil",
                mostrarFlechaAtras = true,
                alVolver = alVolver,
                nombreUsuario = nombreUsuarioBarra,
                fotoPerfilUrl = fotoPerfilUrlBarra,
                revisionCacheFoto = revisionCacheFotoBarra,
                alAbrirMenuPerfil = alAbrirMenuPerfil,
            )
        },
        contenido = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Tu perfil público",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                uiState.mensajeError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
                uiState.mensajeOk?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.primary)
                }

                val descripcionEditarFoto = "Cambiar foto de perfil"
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClickLabel = descripcionEditarFoto,
                            role = Role.Button,
                            onClick = alPulsarCambiarFotoPerfil,
                        ),
                ) {
                    AvatarPerfilCircularApp(
                        nombreUsuario = nombreUsuarioBarra,
                        fotoPerfilUrl = fotoPerfilUrlBarra,
                        tamano = 104.dp,
                        revisionCacheFoto = revisionCacheFotoBarra,
                    )
                    if (cargandoFotoPerfil) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(44.dp),
                            strokeWidth = 3.dp,
                        )
                    }
                }

                mensajeErrorFotoPerfil?.let { texto ->
                    Text(
                        text = texto,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.cargando) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    return@Column
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Los pacientes verán este texto al elegirte o visitar tu ficha.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        CampoTextoBaseApp(
                            valor = uiState.descripcion,
                            alCambiar = viewModel::alCambiarDescripcion,
                            etiqueta = "Descripción",
                            placeholder = "Cuéntale a tus pacientes tu enfoque, especialidades, etc.",
                            singleLine = false,
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Especialidades",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${uiState.especialidades.size}/${LimitesCaracteresRegistro.Psicologo.MAX_ESPECIALIDADES}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        EditorEspecialidadesApp(
                            especialidades = uiState.especialidades,
                            especialidadInput = uiState.especialidadInput,
                            errorEspecialidadInput = uiState.errorEspecialidadInput,
                            habilitado = !uiState.guardando,
                            maxEspecialidades = LimitesCaracteresRegistro.Psicologo.MAX_ESPECIALIDADES,
                            alCambiarInput = viewModel::alCambiarEspecialidadInput,
                            alAnadir = viewModel::alAnadirEspecialidad,
                            alEliminar = viewModel::alEliminarEspecialidad,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                BotonPrimarioApp(
                    texto = if (uiState.guardando) "Guardando…" else "Guardar cambios",
                    alPulsar = viewModel::guardar,
                    habilitado = uiState.hayCambios && !uiState.guardando,
                    cargando = uiState.guardando,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
