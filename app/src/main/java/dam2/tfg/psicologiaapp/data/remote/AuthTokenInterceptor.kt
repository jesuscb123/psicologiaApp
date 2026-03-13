package dam2.tfg.psicologiaapp.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor de OkHttp que añade la cabecera Authorization con el token
 * de Firebase a todas las peticiones al backend.
 */
class AuthTokenInterceptor @Inject constructor(
    private val proveedorToken: ProveedorTokenFirebase
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { proveedorToken.obtenerToken() }
        val solicitudOriginal = chain.request()
        val solicitud = if (token != null) {
            solicitudOriginal.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            solicitudOriginal
        }
        return chain.proceed(solicitud)
    }
}
