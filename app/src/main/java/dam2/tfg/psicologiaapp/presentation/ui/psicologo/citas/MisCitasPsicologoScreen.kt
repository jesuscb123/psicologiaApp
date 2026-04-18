package dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.presentation.components.BotonSecundarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp
import dam2.tfg.psicologiaapp.presentation.ui.citas.FiltroMisCitas
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MisCitasPsicologoScreen(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: MisCitasPsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.recargar() }

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = uiState.filtroSeleccionado == FiltroMisCitas.ACTIVAS,
                    onClick = { viewModel.cambiarFiltro(FiltroMisCitas.ACTIVAS) },
                    label = { Text("Activas") },
                )
                FilterChip(
                    selected = uiState.filtroSeleccionado == FiltroMisCitas.FINALIZADAS,
                    onClick = { viewModel.cambiarFiltro(FiltroMisCitas.FINALIZADAS) },
                    label = { Text("Finalizadas") },
                )
            }

            BotonSecundarioApp(
                texto = if (uiState.cargando) "Actualizando…" else "Actualizar lista",
                alPulsar = { viewModel.recargar() },
                habilitado = !uiState.cargando,
                cargando = uiState.cargando,
                modifier = Modifier.fillMaxWidth(),
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    val titulo =
                        when (uiState.filtroSeleccionado) {
                            FiltroMisCitas.ACTIVAS -> "Activas"
                            FiltroMisCitas.FINALIZADAS -> "Finalizadas"
                        }
                    Text(titulo, style = MaterialTheme.typography.titleMedium)
                }

                if (citasVisibles.isEmpty() && !uiState.cargando) {
                    val textoVacio =
                        when (uiState.filtroSeleccionado) {
                            FiltroMisCitas.ACTIVAS -> "No hay citas activas"
                            FiltroMisCitas.FINALIZADAS -> "No hay citas finalizadas"
                        }
                    item { Text(textoVacio, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(
                        items = citasVisibles,
                        key = { it.id },
                    ) { cita ->
                        TarjetaCitaPsicologo(cita = cita)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaCitaPsicologo(
    cita: Cita,
) {
    TarjetaApp(modifier = Modifier.fillMaxWidth(), mostrarBorde = false) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Paciente: ${cita.nombrePaciente}", style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Inicio: ${formatearIso(cita.inicio)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Estado: ${cita.estadoCalculado}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatearIso(isoOffset: String): String =
    runCatching {
        // El backend devuelve inicio/fin en UTC; mostramos siempre en hora local del dispositivo.
        val instant = OffsetDateTime.parse(isoOffset).toInstant()
        instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }.getOrElse { isoOffset }

