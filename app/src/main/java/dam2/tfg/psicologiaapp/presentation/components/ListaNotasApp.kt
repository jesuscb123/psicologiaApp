package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    var expanded by rememberSaveable(nota.id) { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .animateContentSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = nota.asunto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = if (expanded) {
                            Icons.Filled.KeyboardArrowUp
                        } else {
                            Icons.Filled.KeyboardArrowDown
                        },
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = nota.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = formatearFechaNota(nota.ultimaModificacion),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                )
            }
        }
    }
}

private val formatoFechaNota = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.getDefault())

private fun formatearFechaNota(fechaIso: String): String {
    val fecha = runCatching { OffsetDateTime.parse(fechaIso).toLocalDate() }
        .recoverCatching { LocalDateTime.parse(fechaIso).toLocalDate() }
        .recoverCatching {
            Instant.parse(fechaIso).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        .getOrNull()

    return fecha?.format(formatoFechaNota) ?: fechaIso
}
