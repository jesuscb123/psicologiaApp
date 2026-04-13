package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

@Composable
fun PantallaAnadirTareaPsicologo(
    alTareaGuardada: () -> Unit,
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: AnadirTareaPsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.eventoNavegacion) {
        when (uiState.eventoNavegacion) {
            EventoNavegacionAnadirTareaPsicologo.TareaGuardada -> {
                viewModel.alConsumirEventoNavegacion()
                alTareaGuardada()
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
        contenido = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Nueva tarea",
                    style = MaterialTheme.typography.titleMedium,
                )

                CampoTextoApp(
                    valor = uiState.titulo,
                    alCambiar = viewModel::alCambiarTitulo,
                    etiqueta = "Título",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                CampoTextoApp(
                    valor = uiState.descripcion,
                    alCambiar = viewModel::alCambiarDescripcion,
                    etiqueta = "Descripción",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 4,
                )

                uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Button(
                    onClick = viewModel::guardarTarea,
                    enabled = uiState.esFormularioValido && !uiState.cargando,
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (uiState.cargando) "Guardando..." else "Guardar tarea")
                }
            }
        },
    )
}
