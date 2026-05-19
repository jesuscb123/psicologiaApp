package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val primerApellido = psicologo.apellidos
        .trim()
        .split(Regex("\\s+"))
        .firstOrNull()
        .orEmpty()
    TarjetaApp(
        elevacion = 1.dp,
        modifier = modifier.clickable { alPulsar(psicologo) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 168.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AvatarInicialApp(nombre = nombreCompleto)
            Text(
                text = psicologo.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Text(
                text = primerApellido,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Text(
                text = psicologo.especialidades.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

