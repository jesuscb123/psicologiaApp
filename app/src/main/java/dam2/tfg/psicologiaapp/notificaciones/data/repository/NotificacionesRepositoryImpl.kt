package dam2.tfg.psicologiaapp.notificaciones.data.repository

import dam2.tfg.psicologiaapp.notificaciones.data.remote.EliminarFcmTokenRequestDto
import dam2.tfg.psicologiaapp.notificaciones.data.remote.FcmFuenteDatos
import dam2.tfg.psicologiaapp.notificaciones.data.remote.NotificacionesApi
import dam2.tfg.psicologiaapp.notificaciones.data.remote.NotificarMensajeChatRequestDto
import dam2.tfg.psicologiaapp.notificaciones.data.remote.RegistrarFcmTokenRequestDto
import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionesRepositoryImpl @Inject constructor(
    private val notificacionesApi: NotificacionesApi,
    private val fcmFuenteDatos: FcmFuenteDatos,
) : NotificacionesRepository {

    override suspend fun registrarTokenActual(token: String): Result<Unit> = ejecutarComoResultado {
        val instalacionId = fcmFuenteDatos.obtenerIdInstalacion()
        val respuesta = notificacionesApi.registrarToken(
            RegistrarFcmTokenRequestDto(
                token = token,
                instalacionId = instalacionId,
                plataforma = PLATAFORMA,
            ),
        )
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("HTTP ${respuesta.code()} al registrar token FCM")
        }
    }

    override suspend fun darDeBajaToken(token: String): Result<Unit> = ejecutarComoResultado {
        val respuesta = notificacionesApi.eliminarToken(EliminarFcmTokenRequestDto(token = token))
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("HTTP ${respuesta.code()} al dar de baja token FCM")
        }
    }

    override suspend fun obtenerTokenFcmActual(): Result<String> = ejecutarComoResultado {
        val token = fcmFuenteDatos.obtenerTokenActual().trim()
        if (token.isEmpty()) throw IllegalStateException("FCM devolvió un token vacío")
        token
    }

    override suspend fun notificarMensajeChat(
        chatId: String,
        vistaPreviaTexto: String,
    ): Result<Unit> = ejecutarComoResultado {
        val respuesta = notificacionesApi.notificarMensajeChat(
            NotificarMensajeChatRequestDto(
                chatId = chatId,
                vistaPreviaTexto = vistaPreviaTexto,
            ),
        )
        if (!respuesta.isSuccessful) {
            throw IllegalStateException(
                "HTTP ${respuesta.code()} al notificar mensaje del chat $chatId",
            )
        }
    }

    private suspend inline fun <T> ejecutarComoResultado(crossinline bloque: suspend () -> T): Result<T> = try {
        Result.success(bloque())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }

    private companion object {
        const val PLATAFORMA = "ANDROID"
    }
}
