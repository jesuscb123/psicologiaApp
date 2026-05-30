package dam2.tfg.psicologiaapp.presentation.ui.citas

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun calcularProximaCitaActiva(citas: List<Cita>): Cita? {
    val ahora = Instant.now()
    return citas
        .asSequence()
        .filter { it.estadoCalculado == EstadoCitaCalculado.ACTIVA }
        .mapNotNull { cita ->
            val inicio = runCatching { OffsetDateTime.parse(cita.inicio).toInstant() }
                .getOrNull() ?: return@mapNotNull null
            if (inicio.isBefore(ahora)) return@mapNotNull null
            cita to inicio
        }
        .minByOrNull { it.second }
        ?.first
}

fun calcularHistorialPreview(citas: List<Cita>, limite: Int = 2): List<Cita> =
    citas
        .asSequence()
        .filter { it.estadoCalculado == EstadoCitaCalculado.FINALIZADA }
        .sortedByDescending { cita ->
            runCatching { OffsetDateTime.parse(cita.inicio).toInstant() }.getOrNull()
        }
        .take(limite)
        .toList()

fun generarDiasLaborablesProximos(
    cantidad: Int = 7,
    desde: LocalDate = LocalDate.now(),
): List<LocalDate> {
    val dias = mutableListOf<LocalDate>()
    var fecha = desde
    while (dias.size < cantidad) {
        if (fecha.dayOfWeek != DayOfWeek.SATURDAY && fecha.dayOfWeek != DayOfWeek.SUNDAY) {
            dias.add(fecha)
        }
        fecha = fecha.plusDays(1)
    }
    return dias
}

fun formatearInicioCitaResumen(isoOffset: String): String =
    runCatching {
        OffsetDateTime.parse(isoOffset).toInstant()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }.getOrElse { isoOffset }

fun formatearRangoHistorialSesion(inicioIso: String, finIso: String): String {
    val zona = ZoneId.systemDefault()
    val inicio = runCatching {
        OffsetDateTime.parse(inicioIso).toInstant().atZone(zona)
    }.getOrNull()
    val fin = runCatching {
        OffsetDateTime.parse(finIso).toInstant().atZone(zona)
    }.getOrNull()

    if (inicio == null) return inicioIso
    val fecha = inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val horaInicio = inicio.format(DateTimeFormatter.ofPattern("HH:mm"))
    val horaFin = fin?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: horaInicio
    return "$fecha · $horaInicio-$horaFin"
}
