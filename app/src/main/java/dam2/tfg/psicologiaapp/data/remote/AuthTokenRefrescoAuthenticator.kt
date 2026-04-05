package dam2.tfg.psicologiaapp.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Ante 401 del backend, reintenta una sola vez obteniendo un idToken fresco.
 * También cubre el caso en que la petición salió **sin** Authorization porque
 * [AuthTokenInterceptor] no obtuvo token en el primer intento (p. ej. carrera con otras peticiones).
 */
class AuthTokenRefrescoAuthenticator @Inject constructor(
    private val proveedorToken: ProveedorTokenFirebase
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null
        val solicitud = response.request
        if (solicitud.header(CABECERA_REINTENTO_TOKEN) != null) return null

        val nuevoToken = runBlocking { proveedorToken.obtenerToken(forzarRenovacion = true) }
            ?: return null

        return solicitud.newBuilder()
            .removeHeader("Authorization")
            .header("Authorization", "Bearer $nuevoToken")
            .header(CABECERA_REINTENTO_TOKEN, "1")
            .build()
    }

    companion object {
        const val CABECERA_REINTENTO_TOKEN = "X-Auth-Reintento-Token"
    }
}
