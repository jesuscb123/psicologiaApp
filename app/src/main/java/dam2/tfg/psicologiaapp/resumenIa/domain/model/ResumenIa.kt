package dam2.tfg.psicologiaapp.resumenIa.domain.model

/**
 * Modelo de dominio del resumen IA generado a partir de las últimas notas
 * de un paciente. Alineado con `ResumenIaResponse` del backend.
 *
 * `generadoEn` se mantiene como String (ISO-8601) para que el dominio quede
 * libre de dependencias de formateo de fechas; la capa de presentación
 * decide cómo mostrarlo.
 */
data class ResumenIa(
    val resumen: String,
    val numeroNotasAnalizadas: Int,
    val generadoEn: String,
    val modelo: String,
)
