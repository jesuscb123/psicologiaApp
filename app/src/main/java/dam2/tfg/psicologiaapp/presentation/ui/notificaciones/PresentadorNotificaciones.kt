package dam2.tfg.psicologiaapp.presentation.ui.notificaciones

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import dam2.tfg.psicologiaapp.MainActivity
import dam2.tfg.psicologiaapp.R

/**
 * Muestra las notificaciones cuando la app está en primer plano (FCM no las pinta solo en ese
 * caso) y construye un PendingIntent que abre la pantalla correcta al tocarla.
 *
 * Las claves de los extras deben mantenerse sincronizadas con
 * [ClavesIntentNotificacion] para que MainActivity las lea correctamente.
 */
object PresentadorNotificaciones {

    private const val ID_BASE_TAREA = 100_000
    private const val ID_BASE_CHAT = 200_000
    private const val ID_BASE_RIESGO = 300_000

    fun mostrarNotificacionMensajeChat(
        context: Context,
        chatId: String,
        pacienteId: Long,
        psicologoId: Long,
        titulo: String,
        cuerpo: String,
    ) {
        if (!tienePermisoNotificaciones(context)) return
        GestorCanalesNotificacion.asegurarCanales(context)

        val pendingIntent = construirPendingIntentChat(context, chatId, pacienteId, psicologoId)
        val notif = NotificationCompat.Builder(context, GestorCanalesNotificacion.CANAL_CHAT_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo.ifBlank { context.getString(R.string.app_name) })
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService<NotificationManager>() ?: return
        val id = ID_BASE_CHAT + chatId.hashCode()
        manager.notify("chat-$chatId", id, notif)
    }

    fun mostrarNotificacionTareaNueva(
        context: Context,
        tareaId: Long,
        titulo: String,
        cuerpo: String,
    ) {
        if (!tienePermisoNotificaciones(context)) return
        GestorCanalesNotificacion.asegurarCanales(context)

        val pendingIntent = construirPendingIntentTareas(context, tareaId)
        val notif = NotificationCompat.Builder(context, GestorCanalesNotificacion.CANAL_TAREAS_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo.ifBlank { context.getString(R.string.app_name) })
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService<NotificationManager>() ?: return
        val id = ID_BASE_TAREA + tareaId.toInt()
        manager.notify("tarea-$tareaId", id, notif)
    }

    /**
     * Notificación de alerta clínica (indicios de riesgo). Llega solo al psicólogo. El cuerpo
     * NO contiene contenido de notas; al tocarla se abre la ficha del paciente.
     */
    fun mostrarNotificacionAlertaRiesgo(
        context: Context,
        pacienteId: Long,
        nombrePaciente: String,
        titulo: String,
        cuerpo: String,
    ) {
        if (!tienePermisoNotificaciones(context)) return
        GestorCanalesNotificacion.asegurarCanales(context)

        val pendingIntent = construirPendingIntentFichaPaciente(context, pacienteId, nombrePaciente)
        val notif = NotificationCompat.Builder(context, GestorCanalesNotificacion.CANAL_ALERTAS_RIESGO_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo.ifBlank { context.getString(R.string.app_name) })
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColor(0xFFC62828.toInt())
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService<NotificationManager>() ?: return
        // Mismo tag que el backend ("riesgo-<id>") para que un push posterior reemplace el anterior.
        val id = ID_BASE_RIESGO + pacienteId.toInt()
        manager.notify("riesgo-$pacienteId", id, notif)
    }

    fun construirPendingIntentChat(
        context: Context,
        chatId: String,
        pacienteId: Long,
        psicologoId: Long,
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(ClavesIntentNotificacion.EXTRA_TIPO, ClavesIntentNotificacion.TIPO_CHAT)
            putExtra(ClavesIntentNotificacion.EXTRA_CHAT_ID, chatId)
            putExtra(ClavesIntentNotificacion.EXTRA_PACIENTE_ID, pacienteId)
            putExtra(ClavesIntentNotificacion.EXTRA_PSICOLOGO_ID, psicologoId)
        }
        return PendingIntent.getActivity(
            context,
            chatId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    fun construirPendingIntentTareas(context: Context, tareaId: Long): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(ClavesIntentNotificacion.EXTRA_TIPO, ClavesIntentNotificacion.TIPO_TAREA)
            putExtra(ClavesIntentNotificacion.EXTRA_TAREA_ID, tareaId)
        }
        return PendingIntent.getActivity(
            context,
            tareaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    fun construirPendingIntentFichaPaciente(
        context: Context,
        pacienteId: Long,
        nombrePaciente: String,
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(ClavesIntentNotificacion.EXTRA_TIPO, ClavesIntentNotificacion.TIPO_RIESGO)
            putExtra(ClavesIntentNotificacion.EXTRA_PACIENTE_ID, pacienteId)
            putExtra(ClavesIntentNotificacion.EXTRA_NOMBRE_PACIENTE, nombrePaciente)
        }
        // requestCode dedicado para que no colisione con tareas o chats con id similar.
        val requestCode = (ID_BASE_RIESGO + pacienteId.toInt())
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun tienePermisoNotificaciones(context: Context): Boolean {
        // En Android 13+ se requiere POST_NOTIFICATIONS. Si el usuario lo ha denegado no podemos
        // mostrar la notificación, pero el push igualmente queda registrado en logs.
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
