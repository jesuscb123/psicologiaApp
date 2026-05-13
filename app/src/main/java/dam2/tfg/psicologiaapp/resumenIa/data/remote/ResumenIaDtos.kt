package dam2.tfg.psicologiaapp.resumenIa.data.remote

/**
 * DTO remoto del resumen IA, alineado con `ResumenIaResponse` del backend.
 *
 * `generadoEn` viaja como String (ISO-8601) tal y como Spring serializa
 * `LocalDateTime` por defecto; el dominio decidirá si lo formatea o no.
 */

data class ResumenIaResponseDto(
    val resumen: String,
    val numeroNotasAnalizadas: Int,
    val generadoEn: String,
    val modelo: String,
)
