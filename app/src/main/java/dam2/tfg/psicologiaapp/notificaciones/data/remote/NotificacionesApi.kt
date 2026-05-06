package dam2.tfg.psicologiaapp.notificaciones.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificacionesApi {

    /** Registra (o renueva) el token FCM del usuario autenticado. */
    @POST("api/notificaciones/fcm/token")
    suspend fun registrarToken(@Body request: RegistrarFcmTokenRequestDto): Response<Unit>

    /** Da de baja el token FCM cuando el usuario cierra sesión. */
    @POST("api/notificaciones/fcm/token/baja")
    suspend fun eliminarToken(@Body request: EliminarFcmTokenRequestDto): Response<Unit>

    /** Notifica al destinatario del chat tras escribir un mensaje en RTDB. */
    @POST("api/chats/notificar")
    suspend fun notificarMensajeChat(@Body request: NotificarMensajeChatRequestDto): Response<Unit>
}

data class RegistrarFcmTokenRequestDto(
    val token: String,
    val instalacionId: String?,
    val plataforma: String,
)

data class EliminarFcmTokenRequestDto(
    val token: String,
)

data class NotificarMensajeChatRequestDto(
    val chatId: String,
    val vistaPreviaTexto: String,
)
