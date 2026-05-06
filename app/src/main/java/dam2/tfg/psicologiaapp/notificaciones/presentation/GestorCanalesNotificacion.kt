package dam2.tfg.psicologiaapp.notificaciones.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import dam2.tfg.psicologiaapp.R

/**
 * Crea (idempotentemente) los canales de notificación que utiliza la app.
 * Los identificadores deben coincidir con los que envía el backend en `AndroidNotification.channelId`.
 */
object GestorCanalesNotificacion {

    const val CANAL_CHAT_ID = "chat"
    const val CANAL_TAREAS_ID = "tareas"

    fun asegurarCanales(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return

        val canalChat = NotificationChannel(
            CANAL_CHAT_ID,
            context.getString(R.string.canal_notif_chat_nombre),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.canal_notif_chat_descripcion)
            enableLights(true)
            enableVibration(true)
        }

        val canalTareas = NotificationChannel(
            CANAL_TAREAS_ID,
            context.getString(R.string.canal_notif_tareas_nombre),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.canal_notif_tareas_descripcion)
            enableLights(true)
            enableVibration(true)
        }

        manager.createNotificationChannels(listOf(canalChat, canalTareas))
    }
}
