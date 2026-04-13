package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.AvatarInicialApp
import dam2.tfg.psicologiaapp.presentation.components.BotonPrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaApp

private enum class PestanaPerfilPsicologoPaciente {
    DESCRIPCION,
    RESENAS,
}

@OptIn(ExperimentalMaterial3Api::class)
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
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (uiState.cargando) {
                Text("Cargando...")
                return@Column
            }

            val psicologo = uiState.psicologo
            if (psicologo == null) {
                Text("No se encontró el psicólogo")
                return@Column
            }

            var pestanaActual by rememberSaveable { mutableStateOf(PestanaPerfilPsicologoPaciente.DESCRIPCION) }

            val nombreCompleto = listOf(psicologo.nombre, psicologo.apellidos)
                .filter { it.isNotBlank() }
                .joinToString(" ")

            TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarInicialApp(nombre = nombreCompleto, tamano = 56.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = nombreCompleto,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = psicologo.especialidad,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Nº colegiado: ${psicologo.numeroColegiado}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = pestanaActual == PestanaPerfilPsicologoPaciente.DESCRIPCION,
                    onClick = { pestanaActual = PestanaPerfilPsicologoPaciente.DESCRIPCION },
                    label = { Text("Descripción") },
                )
                FilterChip(
                    selected = pestanaActual == PestanaPerfilPsicologoPaciente.RESENAS,
                    onClick = { pestanaActual = PestanaPerfilPsicologoPaciente.RESENAS },
                    label = { Text("Reseñas") },
                )
            }

            when (pestanaActual) {
                PestanaPerfilPsicologoPaciente.DESCRIPCION -> {
                    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Descripción",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = psicologo.descripcion?.takeIf { it.isNotBlank() } ?: "Este psicólogo todavía no ha añadido una descripción.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                PestanaPerfilPsicologoPaciente.RESENAS -> {
                    TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = "Reseñas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Ver reseñas (próximamente)")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            BotonPrimarioApp(
                texto = if (uiState.asignando) "Asignando..." else "Asignar psicólogo",
                alPulsar = viewModel::asignarPsicologo,
                habilitado = !uiState.asignando,
                cargando = uiState.asignando,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

