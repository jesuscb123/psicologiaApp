package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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

    val activas = uiState.citas.filter { it.estadoCalculado == EstadoCitaCalculado.ACTIVA }
    val finalizadas = uiState.citas.filter { it.estadoCalculado == EstadoCitaCalculado.FINALIZADA }
    val canceladas = uiState.citas.filter { it.estadoCalculado == EstadoCitaCalculado.CANCELADA }

    PantallaConCabeceraOndaApp(
        encabezado = {
            EncabezadoUsuarioApp(
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

            Button(
                onClick = { viewModel.recargar() },
                enabled = !uiState.cargando,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (uiState.cargando) "Cargando..." else "Refrescar") }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    Text("Activas", style = MaterialTheme.typography.titleMedium)
                }
                if (activas.isEmpty()) {
                    item { Text("No tienes citas activas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(activas) { cita ->
                        TarjetaCitaPaciente(
                            cita = cita,
                            mostrarCancelar = true,
                            alCancelar = { citaPendienteCancelar = cita },
                        )
                    }
                }

                item {
                    Text("Finalizadas", style = MaterialTheme.typography.titleMedium)
                }
                if (finalizadas.isEmpty()) {
                    item { Text("No hay citas finalizadas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(finalizadas) { cita ->
                        TarjetaCitaPaciente(cita = cita, mostrarCancelar = false, alCancelar = {})
                    }
                }

                item {
                    Text("Canceladas", style = MaterialTheme.typography.titleMedium)
                }
                if (canceladas.isEmpty()) {
                    item { Text("No hay citas canceladas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(canceladas) { cita ->
                        TarjetaCitaPaciente(cita = cita, mostrarCancelar = false, alCancelar = {})
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaCitaPaciente(
    cita: Cita,
    mostrarCancelar: Boolean,
    alCancelar: () -> Unit,
) {
    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Psicólogo: ${cita.nombrePsicologo}", style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Inicio: ${formatearIso(cita.inicio)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Fin: ${formatearIso(cita.fin)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Estado: ${cita.estadoCalculado}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (mostrarCancelar) {
                Button(
                    onClick = alCancelar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) { Text("Cancelar") }
            }
        }
    }
}

private fun formatearIso(isoOffset: String): String =
    runCatching {
        OffsetDateTime.parse(isoOffset).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }.getOrElse { isoOffset }

