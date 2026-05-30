package dam2.tfg.psicologiaapp.presentation.components

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFechaLista = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.getDefault())

private fun parsearFechaIsoLocal(fechaIso: String): LocalDate? =
    runCatching { OffsetDateTime.parse(fechaIso).toLocalDate() }
        .recoverCatching { LocalDateTime.parse(fechaIso).toLocalDate() }
        .recoverCatching {
            Instant.parse(fechaIso).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        .getOrNull()

fun parsearFechaNotaLocal(fechaIso: String): LocalDate? = parsearFechaIsoLocal(fechaIso)

fun formatearFechaLista(fechaIso: String): String {
    val fecha = parsearFechaIsoLocal(fechaIso)
    return fecha?.format(formatoFechaLista) ?: fechaIso
}
