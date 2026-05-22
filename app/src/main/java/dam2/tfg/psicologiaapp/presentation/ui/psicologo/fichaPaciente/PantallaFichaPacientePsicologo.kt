package dam2.tfg.psicologiaapp.presentation.ui.psicologo.fichaPaciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dam2.tfg.psicologiaapp.presentation.components.BotonFlotantePrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.EstadoVacioContenidoApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.ListaTareasApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.PestanasPildoraDosOpcionesApp
import dam2.tfg.psicologiaapp.presentation.components.TarjetaResumenIaApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFichaPacientePsicologo(
    alVolver: () -> Unit,
    alIrAnadirTarea: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: FichaPacientePsicologoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.recargar()
    }

    val tituloPaciente = uiState.nombreUsuarioPaciente.ifBlank { "Paciente" }

    Box(modifier = Modifier.fillMaxSize()) {
        PantallaConCabeceraOndaApp(
            encabezado = { },
            cabecera = {
                EncabezadoUsuarioApp(
                    tituloCentro = tituloPaciente,
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
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    uiState.mensajeError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    if (uiState.cargando) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        return@Column
                    }

                    PestanasPildoraDosOpcionesApp(
                        primeraEtiqueta = "Notas",
                        segundaEtiqueta = "Tareas",
                        indiceSeleccionado = when (uiState.pestanaActual) {
                            PestanaFichaPacientePsi.NOTAS -> 0
                            PestanaFichaPacientePsi.TAREAS -> 1
                        },
                        alSeleccionarPrimera = { viewModel.cambiarPestana(PestanaFichaPacientePsi.NOTAS) },
                        alSeleccionarSegunda = { viewModel.cambiarPestana(PestanaFichaPacientePsi.TAREAS) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (uiState.pestanaActual) {
                            PestanaFichaPacientePsi.NOTAS -> {
                                if (uiState.notas.isEmpty()) {
                                    EstadoVacioContenidoApp(
                                        titulo = "Sin notas del paciente",
                                        subtitulo = "Cuando registre entradas en su diario, las verás en esta pestaña.",
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        TarjetaResumenIaApp(
                                            resumen = uiState.resumenIa,
                                            cargando = uiState.cargandoResumenIa,
                                            error = uiState.errorResumenIa,
                                            numeroNotasAnalizadas = uiState.numeroNotasAnalizadasIa,
                                            alGenerar = viewModel::generarResumenIa,
                                            alDescartar = viewModel::descartarResumenIa,
                                        )
                                        ListaNotasApp(
                                            notas = uiState.notas,
                                            permitirEliminar = false,
                                            paddingContenido = PaddingValues(bottom = 88.dp),
                                        )
                                    }
                                }
                            }

                            PestanaFichaPacientePsi.TAREAS -> {
                                if (uiState.tareas.isEmpty()) {
                                    EstadoVacioContenidoApp(
                                        titulo = "Sin tareas asignadas",
                                        subtitulo = "Pulsa + para crear la primera tarea y acompañar el seguimiento.",
                                    )
                                } else {
                                    ListaTareasApp(
                                        tareas = uiState.tareas,
                                        paddingContenido = PaddingValues(bottom = 88.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )

        if (uiState.pestanaActual == PestanaFichaPacientePsi.TAREAS && !uiState.cargando) {
            BotonFlotantePrimarioApp(
                alPulsar = alIrAnadirTarea,
                descripcionIcono = "Añadir tarea",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }
}
