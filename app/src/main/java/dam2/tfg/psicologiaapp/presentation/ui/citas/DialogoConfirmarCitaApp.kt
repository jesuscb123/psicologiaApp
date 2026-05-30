package dam2.tfg.psicologiaapp.presentation.ui.citas

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DialogoConfirmarCitaApp(
    fecha: LocalDate,
    hora: LocalTime,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Confirmar cita") },
        text = {
            Text(
                "¿Deseas reservar una cita el ${
                    fecha.format(
                        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES")),
                    )
                } a las ${hora.format(DateTimeFormatter.ofPattern("HH:mm"))}?",
            )
        },
        confirmButton = {
            TextButton(onClick = alConfirmar) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text("Cancelar") }
        },
    )
}
