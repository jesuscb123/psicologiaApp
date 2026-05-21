package dam2.tfg.psicologiaapp.notificaciones.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.MarcarAlertaRiesgoUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.RegistrarFcmTokenActualUseCase
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.ClavesIntentNotificacion
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.PresentadorNotificaciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FCMService"

/**
 * Servicio de Firebase Cloud Messaging.
 *
 *  - [onNewToken] se invoca cuando el SDK genera un token nuevo (instalación, reinstalación,
 *    rotación) y lo registramos en el backend si hay un usuario autenticado.
 *  - [onMessageReceived] se invoca cuando llega un push **con la app en primer plano**
 *    (en background el sistema dibuja la notificación por sí mismo gracias al payload de
 *    `notification` que envía el backend). Mostramos manualmente la notificación de chat o
 *    tarea con su deeplink al destino correcto.
 */
@AndroidEntryPoint
class PsicologiaFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var registrarFcmTokenActualUseCase: RegistrarFcmTokenActualUseCase

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var marcarAlertaRiesgoUseCase: MarcarAlertaRiesgoUseCase

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Job() + Dispatchers.IO + SupervisorJob())
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Si el usuario aún no ha iniciado sesión, no podemos registrar el token (necesita
        // Authorization Bearer). Cuando inicie sesión el flujo de login lo registrará explícitamente.
        if (firebaseAuth.currentUser == null) {
            Log.d(TAG, "onNewToken con sesión nula — se registrará al iniciar sesión")
            return
        }
        scope.launch {
            registrarFcmTokenActualUseCase(token).onFailure { e ->
                Log.w(TAG, "No se pudo registrar el token nuevo en backend: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val datos = message.data
        val tipo = datos[CLAVE_TIPO]
        // En Android, cuando la app está en background y el mensaje incluye `notification`,
        // el sistema ya dibuja el aviso. onMessageReceived solo se ejecuta con la app en foreground
        // o si el push es data-only, así que aquí mostramos la notificación manualmente.
        when (tipo) {
            ClavesIntentNotificacion.TIPO_CHAT -> manejarMensajeChat(message)
            ClavesIntentNotificacion.TIPO_TAREA -> manejarMensajeTarea(message)
            ClavesIntentNotificacion.TIPO_RIESGO -> manejarMensajeRiesgo(message)
            else -> Log.d(TAG, "Push recibido sin tipo conocido: $datos")
        }
    }

    private fun manejarMensajeChat(message: RemoteMessage) {
        val chatId = message.data[CLAVE_CHAT_ID].orEmpty()
        if (chatId.isBlank()) return
        val pacienteId = message.data[CLAVE_PACIENTE_ID]?.toLongOrNull() ?: 0L
        val psicologoId = message.data[CLAVE_PSICOLOGO_ID]?.toLongOrNull() ?: 0L
        val titulo = message.notification?.title.orEmpty()
        val cuerpo = message.notification?.body ?: message.data[CLAVE_FALLBACK_CUERPO].orEmpty()
        PresentadorNotificaciones.mostrarNotificacionMensajeChat(
            context = applicationContext,
            chatId = chatId,
            pacienteId = pacienteId,
            psicologoId = psicologoId,
            titulo = titulo,
            cuerpo = cuerpo,
        )
    }

    private fun manejarMensajeTarea(message: RemoteMessage) {
        val tareaId = message.data[CLAVE_TAREA_ID]?.toLongOrNull() ?: 0L
        val titulo = message.notification?.title.orEmpty()
        val cuerpo = message.notification?.body ?: message.data[CLAVE_FALLBACK_CUERPO].orEmpty()
        PresentadorNotificaciones.mostrarNotificacionTareaNueva(
            context = applicationContext,
            tareaId = tareaId,
            titulo = titulo,
            cuerpo = cuerpo,
        )
    }

    private fun manejarMensajeRiesgo(message: RemoteMessage) {
        val pacienteId = message.data[CLAVE_PACIENTE_ID]?.toLongOrNull() ?: 0L
        if (pacienteId <= 0L) {
            Log.w(TAG, "Push de riesgo descartado: pacienteId inválido")
            return
        }
        val nombrePaciente = message.data[CLAVE_NOMBRE_PACIENTE].orEmpty()
        val titulo = message.notification?.title.orEmpty()
        val cuerpo = message.notification?.body ?: message.data[CLAVE_FALLBACK_CUERPO].orEmpty()
        PresentadorNotificaciones.mostrarNotificacionAlertaRiesgo(
            context = applicationContext,
            pacienteId = pacienteId,
            nombrePaciente = nombrePaciente,
            titulo = titulo,
            cuerpo = cuerpo,
        )
        val psicologoUid = firebaseAuth.currentUser?.uid.orEmpty()
        scope.launch {
            try {
                marcarAlertaRiesgoUseCase(psicologoUid, pacienteId)
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo marcar alerta riesgo en RTDB: ${e.message}")
            }
        }
    }

    private companion object {
        const val CLAVE_TIPO = "tipo"
        const val CLAVE_CHAT_ID = "chatId"
        const val CLAVE_PACIENTE_ID = "pacienteId"
        const val CLAVE_PSICOLOGO_ID = "psicologoId"
        const val CLAVE_TAREA_ID = "tareaId"
        const val CLAVE_NOMBRE_PACIENTE = "nombrePaciente"
        const val CLAVE_FALLBACK_CUERPO = "cuerpo"
    }
}
