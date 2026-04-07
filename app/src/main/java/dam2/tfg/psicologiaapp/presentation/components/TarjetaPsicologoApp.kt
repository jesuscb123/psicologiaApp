package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

@Composable
fun TarjetaPsicologoApp(
    psicologo: Psicologo,
    alPulsar: (Psicologo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nombreCompleto = listOf(psicologo.nombre, psicologo.apellidos)
        .filter { it.isNotBlank() }
        .joinToString(" ")
    TarjetaApp(
        elevacion = 1.dp,
        modifier = modifier.clickable { alPulsar(psicologo) },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AvatarInicialApp(nombre = nombreCompleto)
            Text(
                text = nombreCompleto,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            psicologo.descripcion?.takeIf { it.isNotBlank() }?.let { descripcion ->
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

