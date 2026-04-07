package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente

@Composable
fun TarjetaPacienteApp(
    paciente: Paciente,
    alPulsar: (Paciente) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nombreCompleto = listOf(paciente.nombre, paciente.apellidos)
        .filter { it.isNotBlank() }
        .joinToString(" ")
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { alPulsar(paciente) }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AvatarPerfilCircularApp(
                nombreUsuario = nombreCompleto,
                fotoPerfilUrl = paciente.fotoPerfilUrl,
                tamano = 48.dp,
            )
            Text(
                text = nombreCompleto,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
