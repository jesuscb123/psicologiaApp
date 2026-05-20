package dam2.tfg.psicologiaapp.presentation.ui.registro.seleccionRol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.ui.theme.brushGradienteFirma

private enum class RolRegistroOpcion {
    Paciente,
    Psicologo,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSeleccionRolRegistro(
    alElegirPaciente: () -> Unit,
    alElegirPsicologo: () -> Unit,
    alVolver: () -> Unit,
    viewModel: SeleccionRolRegistroViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var rolSeleccionado by remember { mutableStateOf<RolRegistroOpcion?>(null) }

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionSeleccionRol.IrARegistroPaciente -> {
                viewModel.alConsumirEventoNavegacion()
                alElegirPaciente()
            }
            EventoNavegacionSeleccionRol.IrARegistroPsicologo -> {
                viewModel.alConsumirEventoNavegacion()
                alElegirPsicologo()
            }
            EventoNavegacionSeleccionRol.Volver -> {
                viewModel.alConsumirEventoNavegacion()
                alVolver()
            }
            null -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = viewModel::volver,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                val formaCta = MaterialTheme.shapes.extraLarge
                val puedeContinuar = rolSeleccionado != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(formaCta)
                        .then(
                            if (puedeContinuar) {
                                Modifier.background(brushGradienteFirma())
                            } else {
                                Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            },
                        )
                        .clickable(enabled = puedeContinuar) {
                            when (rolSeleccionado) {
                                RolRegistroOpcion.Paciente -> viewModel.elegirPaciente()
                                RolRegistroOpcion.Psicologo -> viewModel.elegirPsicologo()
                                null -> Unit
                            }
                        }
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Continuar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (puedeContinuar) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (puedeContinuar) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 8.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido/a.\n¿Cómo quieres usar la app?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(48.dp))

            TarjetaSeleccionRol(
                titulo = "Soy Paciente",
                descripcion = "Busco un espacio seguro y apoyo profesional para mi bienestar.",
                seleccionado = rolSeleccionado == RolRegistroOpcion.Paciente,
                onClick = { rolSeleccionado = RolRegistroOpcion.Paciente },
                colorIlustracion = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TarjetaSeleccionRol(
                titulo = "Soy Psicólogo",
                descripcion = "Ofrezco acompañamiento y deseo gestionar mis consultas aquí.",
                seleccionado = rolSeleccionado == RolRegistroOpcion.Psicologo,
                onClick = { rolSeleccionado = RolRegistroOpcion.Psicologo },
                colorIlustracion = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun TarjetaSeleccionRol(
    titulo: String,
    descripcion: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    colorIlustracion: Color,
    ilustracion: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val borde = if (seleccionado) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    } else {
        Color.Transparent
    }
    val fondoTarjeta = if (seleccionado) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.5.dp, color = borde, shape = shape),
        shape = shape,
        color = fondoTarjeta,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(colorIlustracion),
                contentAlignment = Alignment.Center,
            ) {
                ilustracion()
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IndicadorRadio(seleccionado = seleccionado)
        }
    }
}

@Composable
private fun IndicadorRadio(seleccionado: Boolean) {
    val borde = if (seleccionado) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Box(
        modifier = Modifier
            .size(28.dp)
            .border(width = 1.5.dp, color = borde, shape = CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (seleccionado) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
