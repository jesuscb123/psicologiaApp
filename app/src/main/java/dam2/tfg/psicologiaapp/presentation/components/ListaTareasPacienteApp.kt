package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

@Composable
fun ListaTareasPacienteApp(
    tareas: List<Tarea>,
    alAceptarTarea: (tareaId: Long) -> Unit,
    alCompletarTarea: (tareaId: Long) -> Unit,
    modifier: Modifier = Modifier,
    paddingContenido: PaddingValues = PaddingValues(bottom = 24.dp),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = paddingContenido,
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = tareas, key = { it.id }) { tarea ->
            TarjetaContenidoTareaPaciente(
                tarea = tarea,
                alAceptarTarea = alAceptarTarea,
                alCompletarTarea = alCompletarTarea,
            )
        }
    }
}

@Composable
private fun TarjetaContenidoTareaPaciente(
    tarea: Tarea,
    alAceptarTarea: (tareaId: Long) -> Unit,
    alCompletarTarea: (tareaId: Long) -> Unit,
) {
    val tareaPendienteAceptar = !tarea.aceptadaPorPaciente && !tarea.realizada
    val tareaPendienteCompletar = tarea.aceptadaPorPaciente && !tarea.realizada
    val colorEstado = when {
        tarea.realizada -> MaterialTheme.colorScheme.primary
        tarea.aceptadaPorPaciente -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
    }

    TarjetaListaExpandibleApp(
        titulo = tarea.titulo,
        claveExpandido = tarea.id,
        contenidoExpandido = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                when {
                    tareaPendienteAceptar -> {
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { alAceptarTarea(tarea.id) },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text("Aceptar")
                        }
                    }
                    tareaPendienteCompletar -> {
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { alCompletarTarea(tarea.id) },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text("Completar")
                        }
                    }
                }
            }
        },
        pie = {
            PieTarjetaListaConIconoApp(
                icono = Icons.Filled.CheckCircle,
                texto = textoEstadoTarea(tarea),
                colorTexto = colorEstado,
                tintIcono = colorEstado,
            )
        },
    )
}
