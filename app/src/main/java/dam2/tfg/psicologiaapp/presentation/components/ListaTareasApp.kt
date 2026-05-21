package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea

@Composable
fun ListaTareasApp(
    tareas: List<Tarea>,
    modifier: Modifier = Modifier,
    paddingContenido: PaddingValues = PaddingValues(bottom = 80.dp),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = paddingContenido,
        modifier = modifier.fillMaxSize(),
    ) {
        items(tareas, key = { it.id }) { tarea ->
            TarjetaContenidoTarea(tarea = tarea)
        }
    }
}

@Composable
private fun TarjetaContenidoTarea(tarea: Tarea) {
    val colorEstado = when {
        tarea.realizada -> MaterialTheme.colorScheme.primary
        tarea.aceptadaPorPaciente -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
    }

    TarjetaListaExpandibleApp(
        titulo = tarea.titulo,
        claveExpandido = tarea.id,
        contenidoExpandido = {
            if (tarea.descripcion.isNotBlank()) {
                Text(
                    text = tarea.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
