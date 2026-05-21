package dam2.tfg.psicologiaapp.presentation.components

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFechaLista = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.getDefault())

fun formatearFechaLista(fechaIso: String): String {
    val fecha = runCatching { OffsetDateTime.parse(fechaIso).toLocalDate() }
        .recoverCatching { LocalDateTime.parse(fechaIso).toLocalDate() }
        .recoverCatching {
            Instant.parse(fechaIso).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        .getOrNull()

    return fecha?.format(formatoFechaLista) ?: fechaIso
}
