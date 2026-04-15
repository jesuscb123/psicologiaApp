package dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp
import java.time.OffsetDateTime
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

            if (uiState.citas.isEmpty() && !uiState.cargando) {
                Text("No hay citas", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(uiState.citas) { cita ->
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
    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
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
        OffsetDateTime.parse(isoOffset).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }.getOrElse { isoOffset }

