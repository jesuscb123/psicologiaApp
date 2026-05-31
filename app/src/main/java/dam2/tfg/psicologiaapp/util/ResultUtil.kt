package dam2.tfg.psicologiaapp.util

import kotlinx.coroutines.CancellationException

/**
 * Como [runCatching], pero no captura [CancellationException] (cancelación de corrutinas al navegar).
 */
inline fun <R> runSuspendCatching(block: () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }

fun Throwable?.mensajeErrorParaUi(fallback: String? = null): String? {
    val error = this ?: return null
    if (error is CancellationException) return null
    val texto = error.message?.trim()?.takeIf { it.isNotEmpty() } ?: return fallback
    if (texto.contains("StandaloneCoroutine", ignoreCase = true) &&
        texto.contains("cancel", ignoreCase = true)
    ) {
        return null
    }
    return texto
}
