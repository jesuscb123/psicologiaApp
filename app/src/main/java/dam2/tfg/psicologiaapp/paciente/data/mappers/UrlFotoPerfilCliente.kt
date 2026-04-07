package dam2.tfg.psicologiaapp.paciente.data.mappers

import dam2.tfg.psicologiaapp.BuildConfig

/**
 * Alinea la URL de foto con [BuildConfig.BASE_URL] si el API devolvió localhost,
 * para que Coil cargue la imagen en el dispositivo.
 */
internal fun normalizarUrlFotoPerfilCliente(url: String?): String? {
    val u = url?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val lower = u.lowercase()
    val apuntaABucleLocal = lower.contains("localhost") || lower.contains("127.0.0.1")
    if (!apuntaABucleLocal) return u
    val idx = u.indexOf("/api/")
    if (idx < 0) return u
    val base = BuildConfig.BASE_URL.trimEnd('/')
    return base + u.substring(idx)
}
