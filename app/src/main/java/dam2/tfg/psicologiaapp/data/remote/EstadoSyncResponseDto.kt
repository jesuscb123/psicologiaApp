package dam2.tfg.psicologiaapp.data.remote

/**
 * Estado mínimo de sincronización para evitar descargas completas si no hay cambios.
 *
 * - [ultimaModificacion] se espera como cadena ISO del backend (p. ej. "2026-04-07T18:22:10.123").
 * - [total] permite detectar borrados aunque no cambie la marca temporal (según implementación backend).
 */
data class EstadoSyncResponseDto(
    val ultimaModificacion: String? = null,
    val total: Long = 0,
)

