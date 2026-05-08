package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = paddingContenido,
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = tareas, key = { it.id }) { tarea ->
            var expanded by rememberSaveable(tarea.id) { mutableStateOf(false) }
            val tareaPendienteAceptar = !tarea.aceptadaPorPaciente && !tarea.realizada
            val tareaPendienteCompletar = tarea.aceptadaPorPaciente && !tarea.realizada

            TarjetaApp(
                elevacion = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = tarea.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (expanded) "Colapsar" else "Expandir",
                            )
                        }
                    }

                    if (expanded && tarea.descripcion.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = tarea.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (expanded) {
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

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = textoEstadoTarea(tarea),
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            tarea.realizada -> MaterialTheme.colorScheme.primary
                            tarea.aceptadaPorPaciente -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}
