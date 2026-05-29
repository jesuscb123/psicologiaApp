package dam2.tfg.psicologiaapp.presentation.ui.paciente.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.presentation.components.BotonFlotantePrimarioApp
import dam2.tfg.psicologiaapp.presentation.components.EncabezadoUsuarioApp
import dam2.tfg.psicologiaapp.presentation.components.EstadoVacioContenidoApp
import dam2.tfg.psicologiaapp.presentation.components.ListaNotasApp
import dam2.tfg.psicologiaapp.presentation.components.PantallaConCabeceraOndaApp
import dam2.tfg.psicologiaapp.presentation.components.coloresOutlinedCampoBusquedaApp
import dam2.tfg.psicologiaapp.presentation.components.formatearFechaLista
import dam2.tfg.psicologiaapp.presentation.components.parsearFechaNotaLocal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotasPacienteScreen(
    alVolver: () -> Unit,
    alIrAAnadirNota: () -> Unit,
    alAbrirMenuPerfil: () -> Unit,
    nombreUsuarioBarra: String,
    fotoPerfilUrlBarra: String?,
    revisionCacheFotoBarra: Long = 0L,
    viewModel: HomePacienteViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.sincronizarSiProcede()
    }

    val uiState by viewModel.uiState.collectAsState()
    var notaPendienteEliminar by remember { mutableStateOf<Nota?>(null) }

    notaPendienteEliminar?.let { nota ->
        AlertDialog(
            onDismissRequest = { notaPendienteEliminar = null },
            title = { Text("Eliminar nota") },
            text = { Text("Esta accion no se puede deshacer. ¿Quieres continuar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarNota(nota.id)
                        notaPendienteEliminar = null
                    },
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { notaPendienteEliminar = null }) { Text("Cancelar") }
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PantallaConCabeceraOndaApp(
            encabezado = {
                EncabezadoUsuarioApp(
                    tituloCentro = "Mis notas",
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
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                uiState.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    when {
                        uiState.cargando -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        uiState.perfilPaciente?.psicologoId == null -> {
                            Text(
                                text = "Aun no tienes psicologo asignado.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.TopStart),
                            )
                        }

                        uiState.notas.isEmpty() -> {
                            EstadoVacioContenidoApp(
                                titulo = "Tu diario está en blanco",
                                subtitulo = "Pulsa el botón + para registrar cómo te sientes y compartirlo con tu psicólogo.",
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

                        else -> {
                            NotasPacienteListaConFiltros(
                                notas = uiState.notas,
                                alSolicitarEliminar = { notaPendienteEliminar = it },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }

        if (!uiState.cargando && uiState.perfilPaciente?.psicologoId != null) {
            BotonFlotantePrimarioApp(
                alPulsar = alIrAAnadirNota,
                descripcionIcono = "Anadir nota",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotasPacienteListaConFiltros(
    notas: List<Nota>,
    alSolicitarEliminar: (Nota) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textoBusqueda by rememberSaveable { mutableStateOf("") }
    var fechaFiltroIso by rememberSaveable { mutableStateOf<String?>(null) }
    val fechaFiltro = remember(fechaFiltroIso) {
        fechaFiltroIso?.let { iso -> runCatching { LocalDate.parse(iso) }.getOrNull() }
    }
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val notasFiltradas = remember(notas, textoBusqueda, fechaFiltro) {
        val q = textoBusqueda.trim().lowercase()
        notas.filter { nota ->
            val coincideTexto = q.isEmpty() ||
                nota.asunto.lowercase().contains(q) ||
                nota.descripcion.lowercase().contains(q)
            val coincideFecha = fechaFiltro == null ||
                parsearFechaNotaLocal(nota.ultimaModificacion) == fechaFiltro
            coincideTexto && coincideFecha
        }
    }

    if (mostrarSelectorFecha) {
        DatePickerDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val fecha = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            fechaFiltroIso = fecha.toString()
                        }
                        mostrarSelectorFecha = false
                    },
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorFecha = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Buscar en título o descripción…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = coloresOutlinedCampoBusquedaApp(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val etiquetaFecha = fechaFiltro?.format(DateTimeFormatter.ISO_LOCAL_DATE)
                ?.let { formatearFechaLista("${it}T00:00:00") }
                ?: "Filtrar por fecha"

            FilterChip(
                selected = fechaFiltro != null,
                onClick = { mostrarSelectorFecha = true },
                label = {
                    Text(
                        text = etiquetaFecha,
                        fontWeight = if (fechaFiltro != null) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )

            if (fechaFiltro != null) {
                FilterChip(
                    selected = false,
                    onClick = { fechaFiltroIso = null },
                    label = { Text("Quitar") },
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (notasFiltradas.isEmpty()) {
                val mensaje = when {
                    textoBusqueda.isNotBlank() && fechaFiltro != null ->
                        "No hay notas que coincidan con tu búsqueda y la fecha seleccionada."
                    textoBusqueda.isNotBlank() ->
                        "No hay notas que coincidan con tu búsqueda."
                    fechaFiltro != null ->
                        "No hay notas en esta fecha."
                    else ->
                        "No hay notas que mostrar."
                }
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                ListaNotasApp(
                    notas = notasFiltradas,
                    modifier = Modifier.fillMaxSize(),
                    paddingContenido = PaddingValues(bottom = 88.dp),
                    permitirEliminar = true,
                    alSolicitarEliminar = alSolicitarEliminar,
                )
            }
        }
    }
}
