package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaPacienteApp

@Composable
fun PantallaHomePsicologo(
    alIrAFichaPaciente: (Long) -> Unit,
    alIrAChatConPaciente: (Long) -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: HomePsicologoViewModel,
) {
    LaunchedEffect(Unit) {
        viewModel.sincronizarSiProcede()
    }

    val uiState by viewModel.uiState.collectAsState()

    PantallaConCabeceraOndaApp(
        modifier = Modifier.fillMaxSize(),
        paddingContenido = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        encabezado = {
            EncabezadoUsuarioApp(
                mostrarIconoMenu = true,
                alAbrirMenu = alAbrirMenuPerfil,
                nombreUsuario = nombreUsuarioBarra,
                fotoPerfilUrl = fotoPerfilUrlBarra,
                revisionCacheFoto = revisionCacheFotoBarra,
                alAbrirMenuPerfil = alAbrirMenuPerfil,
            )
        },
        contenido = {
            when {
                uiState.cargando -> {
                    HomePsicologoContenidoCargando(mensajeError = uiState.mensajeError)
                }

                uiState.listaPacientes.isEmpty() -> {
                    HomePsicologoEstadoVacio(mensajeError = uiState.mensajeError)
                }

                else -> {
                    HomePsicologoListaPacientes(
                        mensajeError = uiState.mensajeError,
                        pacientes = uiState.listaPacientes,
                        mapaCitaProxima = uiState.mapaCitaProxima,
                        alIrAFichaPaciente = alIrAFichaPaciente,
                        alIrAChatConPaciente = alIrAChatConPaciente,
                    )
                }
            }
        },
    )
}

@Composable
private fun HomePsicologoContenidoCargando(mensajeError: String?) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 32.dp),
        ) {
            mensajeError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "Cargando…",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomePsicologoEstadoVacio(mensajeError: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        mensajeError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "No tienes pacientes asignados. Cuando te asignen pacientes, aparecerán aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

@Composable
private fun HomePsicologoListaPacientes(
    mensajeError: String?,
    pacientes: List<Paciente>,
    mapaCitaProxima: Map<Long, Cita?>,
    alIrAFichaPaciente: (Long) -> Unit,
    alIrAChatConPaciente: (Long) -> Unit,
) {
    var busqueda by remember { mutableStateOf("") }
    val pacientesFiltrados = pacientes.filter { paciente ->
        busqueda.isBlank() ||
            paciente.nombre.contains(busqueda, ignoreCase = true) ||
            paciente.apellidos.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        mensajeError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Mis Pacientes",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Consulta y gestiona el progreso de tus pacientes.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        TextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Buscar por nombre...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
        )

        if (pacientesFiltrados.isEmpty()) {
            Text(
                text = "Sin resultados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                items(pacientesFiltrados) { paciente ->
                    TarjetaPacienteApp(
                        paciente = paciente,
                        alPulsar = { alIrAFichaPaciente(paciente.idPaciente) },
                        modifier = Modifier.fillMaxWidth(),
                        citaProxima = mapaCitaProxima[paciente.idPaciente],
                        alPulsarChat = { alIrAChatConPaciente(it.idPaciente) },
                    )
                }
            }
        }
    }
}
