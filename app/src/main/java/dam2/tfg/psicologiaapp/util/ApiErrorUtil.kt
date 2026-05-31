package dam2.tfg.psicologiaapp.util

import retrofit2.HttpException

private val REGEX_ERROR_JSON = Regex(""""error"\s*:\s*"([^"]+)"""")

fun HttpException.mensajeErrorHttp(): String {
    val cuerpo = response()?.errorBody()?.use { it.string() }?.trim().orEmpty()
    if (cuerpo.isNotEmpty()) {
        REGEX_ERROR_JSON.find(cuerpo)?.groupValues?.getOrNull(1)?.let { return it }
        if (!cuerpo.startsWith("{")) return cuerpo.take(200)
    }
    return "Error ${code()}"
}

fun Throwable.mensajeErrorHttpODefecto(defecto: String): String = when (this) {
    is HttpException -> mensajeErrorHttp()
    else -> message?.takeIf { it.isNotBlank() && !it.startsWith("HTTP ") } ?: defecto
}
