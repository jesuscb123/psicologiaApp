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

    /**
     * Canal dedicado a alertas clínicas urgentes (indicios de riesgo detectados por IA).
     * Importance MAX para que pase los filtros de "Modo no molestar" del psicólogo y por la
     * gravedad del aviso debe poder despertar la pantalla.
     */
    const val CANAL_ALERTAS_RIESGO_ID = "alertas_riesgo"

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

        val canalAlertasRiesgo = NotificationChannel(
            CANAL_ALERTAS_RIESGO_ID,
            context.getString(R.string.canal_notif_alertas_riesgo_nombre),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.canal_notif_alertas_riesgo_descripcion)
            enableLights(true)
            enableVibration(true)
            setBypassDnd(true)
        }

        manager.createNotificationChannels(listOf(canalChat, canalTareas, canalAlertasRiesgo))
    }
}
