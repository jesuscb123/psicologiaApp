package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.components.CampoTextoBaseApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp

@Composable
fun PantallaAjustesPsicologo(
    alVolver: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: AjustesPsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
                uiState.mensajeError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
                uiState.mensajeOk?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.primary)
                }

                if (uiState.cargando) {
                    Text("Cargando...")
                    return@Column
                }

                Text(
                    text = "Descripción del perfil",
                    style = MaterialTheme.typography.titleMedium,
                )

                CampoTextoBaseApp(
                    valor = uiState.descripcion,
                    alCambiar = viewModel::alCambiarDescripcion,
                    etiqueta = "Descripción",
                    placeholder = "Cuéntale a tus pacientes tu enfoque, especialidad, etc.",
                    singleLine = false,
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = viewModel::guardar,
                    enabled = uiState.hayCambios && !uiState.guardando,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (uiState.guardando) "Guardando..." else "Guardar",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
    )
}

