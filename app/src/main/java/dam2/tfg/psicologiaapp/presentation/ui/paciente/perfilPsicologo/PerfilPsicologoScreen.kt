package dam2.tfg.psicologiaapp.presentation.ui.paciente.perfilPsicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.AvatarPerfilCircularApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaPerfilPsicologo(
    psicologoId: String,
    alAsignacionCompletada: () -> Unit,
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: PerfilPsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(psicologoId) {
        viewModel.cargar(psicologoId)
    }

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionPerfilPsicologo.AsignacionCompletada -> {
                viewModel.alConsumirEventoNavegacion()
                alAsignacionCompletada()
            }
            null -> Unit
        }
    }

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Perfil",
                mostrarFlechaAtras = true,
                alVolver = alVolver,
                nombreUsuario = nombreUsuarioBarra,
                fotoPerfilUrl = fotoPerfilUrlBarra,
                revisionCacheFoto = revisionCacheFotoBarra,
                alAbrirMenuPerfil = alAbrirMenuPerfil,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
        ) {
            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (uiState.cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                return@Column
            }

            val psicologo = uiState.psicologo
            if (psicologo == null) {
                Text(
                    "No se encontró el psicólogo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                return@Column
            }

            val nombreCompleto = listOf(psicologo.nombre, psicologo.apellidos)
                .filter { it.isNotBlank() }
                .joinToString(" ")

            val mostrarBotonAsignar = !uiState.pacienteYaTienePsicologo
            val paddingInferiorBoton = if (mostrarBotonAsignar) 72.dp else 0.dp

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingInferiorBoton),
                ) {
                    CabeceraPerfilPsicologo(
                        nombreCompleto = nombreCompleto,
                        fotoPerfilUrl = psicologo.fotoPerfilUrl,
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        TarjetaInformacionPsicologo(psicologo = psicologo)
                    }
                }

                if (mostrarBotonAsignar) {
                    BotonPrimarioApp(
                        texto = if (uiState.asignando) "Asignando..." else "Asignar psicólogo",
                        alPulsar = viewModel::asignarPsicologo,
                        habilitado = !uiState.asignando,
                        cargando = uiState.asignando,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CabeceraPerfilPsicologo(
    nombreCompleto: String,
    fotoPerfilUrl: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box {
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                AvatarPerfilCircularApp(
                    nombreUsuario = nombreCompleto,
                    fotoPerfilUrl = fotoPerfilUrl,
                    tamano = 100.dp,
                )
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 2.dp),
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }

        Text(
            text = nombreCompleto,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
            Text(
                text = "Valoración de la comunidad — próximamente",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaInformacionPsicologo(psicologo: Psicologo) {
    val especialidadesTokens = psicologo.especialidades

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Sobre mí",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = psicologo.descripcion?.takeIf { it.isNotBlank() }
                    ?: "Este psicólogo todavía no ha añadido una descripción.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TarjetaInfoContacto(
                    titulo = "Educación / colegiado",
                    subtitulo = psicologo.numeroColegiado.ifBlank { "—" },
                    icono = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    },
                    colorIconoFondo = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.weight(1f),
                )
                TarjetaInfoContacto(
                    titulo = "Disponibilidad",
                    subtitulo = "Consulta horas al agendar una cita.",
                    icono = {
                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    },
                    colorIconoFondo = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f),
                )
            }

            if (especialidadesTokens.isNotEmpty()) {
                Text(
                    text = "Especialidades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    especialidadesTokens.forEach { esp ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        ) {
                            Text(
                                text = esp,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaInfoContacto(
    titulo: String,
    subtitulo: String,
    icono: @Composable () -> Unit,
    colorIconoFondo: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = colorIconoFondo,
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    icono()
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
