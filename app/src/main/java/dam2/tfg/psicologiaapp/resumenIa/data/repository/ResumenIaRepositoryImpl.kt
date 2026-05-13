package dam2.tfg.psicologiaapp.resumenIa.data.repository

import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.resumenIa.data.mappers.toDomain
import dam2.tfg.psicologiaapp.resumenIa.data.remote.ResumenIaApi
import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa
import dam2.tfg.psicologiaapp.resumenIa.domain.repository.ResumenIaRepository
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

/**
 * Implementación del repositorio de resumen IA. No mantiene caché local:
 * cada llamada genera un resumen nuevo en el backend bajo demanda.
 *
 * Sigue el mismo patrón de reintento ante 401 que [NotaRepositoryImpl] para
 * cubrir cualquier carrera del token Firebase, aunque el `Authenticator` de
 * OkHttp ya hace el primer reintento a nivel HTTP.
 */
@Singleton
class ResumenIaRepositoryImpl @Inject constructor(
    private val resumenIaApi: ResumenIaApi,
    private val proveedorTokenFirebase: ProveedorTokenFirebase,
) : ResumenIaRepository {

    override suspend fun generarResumenNotasPaciente(pacienteId: Long): Result<ResumenIa> = runCatching {
        val respuesta = ejecutarConRefrescoTokenSi401 {
            resumenIaApi.generarResumenNotasPaciente(pacienteId)
        }
        if (!respuesta.isSuccessful) {
            val codigo = respuesta.code()
            val detalle = when (codigo) {
                401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                403 -> "HTTP 403: no autorizado para generar el resumen de este paciente"
                404 -> "El paciente no tiene notas para resumir"
                503 -> "El servicio de resumen IA no está disponible en este momento"
                else -> "HTTP $codigo"
            }
            throw IllegalStateException("Error al generar resumen IA: $detalle")
        }
        val cuerpo = respuesta.body()
            ?: throw IllegalStateException("Respuesta vacía del servidor al generar resumen IA")
        cuerpo.toDomain()
    }

    private suspend fun <T> ejecutarConRefrescoTokenSi401(
        bloque: suspend () -> Response<T>
    ): Response<T> {
        var respuesta = bloque()
        if (respuesta.code() == 401) {
            proveedorTokenFirebase.obtenerToken(forzarRenovacion = true)
            respuesta = bloque()
        }
        return respuesta
    }
}
