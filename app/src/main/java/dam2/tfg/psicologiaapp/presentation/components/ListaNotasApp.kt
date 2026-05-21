package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.nota.domain.model.Nota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaNotasApp(
    notas: List<Nota>,
    modifier: Modifier = Modifier,
    paddingContenido: PaddingValues = PaddingValues(bottom = 80.dp),
    permitirEliminar: Boolean = true,
    alSolicitarEliminar: (Nota) -> Unit = {},
    /** Si es true, usa [Column] en lugar de [LazyColumn] (p. ej. dentro de un scroll padre). */
    listaPlana: Boolean = false,
) {
    if (listaPlana) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.padding(paddingContenido),
        ) {
            notas.forEach { nota ->
                ContenidoItemNota(
                    nota = nota,
                    permitirEliminar = permitirEliminar,
                    alSolicitarEliminar = alSolicitarEliminar,
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingContenido,
            modifier = modifier.fillMaxSize()
        ) {
            items(notas, key = { it.id }) { nota ->
                ContenidoItemNota(
                    nota = nota,
                    permitirEliminar = permitirEliminar,
                    alSolicitarEliminar = alSolicitarEliminar,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoItemNota(
    nota: Nota,
    permitirEliminar: Boolean,
    alSolicitarEliminar: (Nota) -> Unit,
) {
    if (permitirEliminar) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { valor ->
                if (valor == SwipeToDismissBoxValue.StartToEnd) {
                    alSolicitarEliminar(nota)
                    false
                } else {
                    true
                }
            }
        )
        val mostrandoRojo =
            dismissState.currentValue != SwipeToDismissBoxValue.Settled ||
                dismissState.targetValue != SwipeToDismissBoxValue.Settled
        val colorFondo by animateColorAsState(
            targetValue = if (mostrandoRojo) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                Color.Transparent
            },
            label = "notaSwipeBackgroundColor"
        )
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = false,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorFondo),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Eliminar",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (mostrandoRojo) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            Color.Transparent
                        }
                    )
                }
            },
            content = {
                TarjetaContenidoNota(nota = nota)
            }
        )
    } else {
        TarjetaContenidoNota(nota = nota)
    }
}

@Composable
private fun TarjetaContenidoNota(nota: Nota) {
    TarjetaListaExpandibleApp(
        titulo = nota.asunto,
        claveExpandido = nota.id,
        contenidoExpandido = {
            if (nota.descripcion.isNotBlank()) {
                Text(
                    text = nota.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        pie = {
            PieTarjetaListaConIconoApp(
                icono = Icons.Filled.DateRange,
                texto = formatearFechaLista(nota.ultimaModificacion),
            )
        },
    )
}
