package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaCitaPacienteApp
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas

@Composable
fun MisCitasPacienteScreen(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: MisCitasPacienteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.recargar() }

    var citaPendienteCancelar by remember { mutableStateOf<Cita?>(null) }

    citaPendienteCancelar?.let { cita ->
        AlertDialog(
            onDismissRequest = { citaPendienteCancelar = null },
            title = { Text("Cancelar cita") },
            text = { Text("¿Seguro que quieres cancelar esta cita?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelarCita(cita.id)
                        citaPendienteCancelar = null
                    }
                ) { Text("Cancelar cita") }
            },
            dismissButton = {
                TextButton(onClick = { citaPendienteCancelar = null }) { Text("Volver") }
            },
        )
    }

    val citasVisibles =
        uiState.citas
            .asSequence()
            .filter { it.estadoCalculado != EstadoCitaCalculado.CANCELADA }
            .filter { cita ->
                when (uiState.filtroSeleccionado) {
                    FiltroMisCitas.ACTIVAS -> cita.estadoCalculado == EstadoCitaCalculado.ACTIVA
                    FiltroMisCitas.FINALIZADAS -> cita.estadoCalculado == EstadoCitaCalculado.FINALIZADA
                }
            }
            .toList()

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
                tituloCentro = "Mis citas",
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Mensaje de error si existe
            uiState.mensajeError?.let { mensaje ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // Filtros mejorados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val contadorActivas = uiState.citas.count {
                    it.estadoCalculado == EstadoCitaCalculado.ACTIVA
                }
                val contadorFinalizadas = uiState.citas.count {
                    it.estadoCalculado == EstadoCitaCalculado.FINALIZADA
                }

                FilterChip(
                    selected = uiState.filtroSeleccionado == FiltroMisCitas.ACTIVAS,
                    onClick = { viewModel.cambiarFiltro(FiltroMisCitas.ACTIVAS) },
                    label = {
                        Text(
                            text = "Activas ($contadorActivas)",
                            fontWeight = if (uiState.filtroSeleccionado == FiltroMisCitas.ACTIVAS)
                                FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
                FilterChip(
                    selected = uiState.filtroSeleccionado == FiltroMisCitas.FINALIZADAS,
                    onClick = { viewModel.cambiarFiltro(FiltroMisCitas.FINALIZADAS) },
                    label = {
                        Text(
                            text = "Finalizadas ($contadorFinalizadas)",
                            fontWeight = if (uiState.filtroSeleccionado == FiltroMisCitas.FINALIZADAS)
                                FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                )
            }

            // Indicador de carga centralizado
            if (uiState.cargando && citasVisibles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                        )
                        Text(
                            text = "Cargando citas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {

                // Lista de citas
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Encabezado de sección
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val titulo = when (uiState.filtroSeleccionado) {
                                FiltroMisCitas.ACTIVAS -> "Activas"
                                FiltroMisCitas.FINALIZADAS -> "Finalizadas"
                            }
                            Text(
                                text = titulo,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (uiState.cargando && citasVisibles.isNotEmpty()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    // Estado vacío mejorado
                    if (citasVisibles.isEmpty() && !uiState.cargando) {
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DateRange,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .padding(24.dp)
                                            .size(48.dp),
                                    )
                                }
                                val textoVacio = when (uiState.filtroSeleccionado) {
                                    FiltroMisCitas.ACTIVAS -> "No tienes citas activas"
                                    FiltroMisCitas.FINALIZADAS -> "No hay citas finalizadas"
                                }
                                Text(
                                    text = textoVacio,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                )
                                val descripcionVacio = when (uiState.filtroSeleccionado) {
                                    FiltroMisCitas.ACTIVAS -> "Aquí aparecerán tus próximas citas"
                                    FiltroMisCitas.FINALIZADAS -> "Las citas completadas se mostrarán aquí"
                                }
                                Text(
                                    text = descripcionVacio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                )
                            }
                        }
                    } else {
                        // Lista de citas
                        items(
                            items = citasVisibles,
                            key = { it.id },
                        ) { cita ->
                            TarjetaCitaPacienteApp(
                                cita = cita,
                                mostrarCancelar = viewModel.puedeCancelar(cita.estadoCalculado),
                                alCancelar = { citaPendienteCancelar = cita },
                            )
                        }
                    }
                }
            }
        }
    }
}
