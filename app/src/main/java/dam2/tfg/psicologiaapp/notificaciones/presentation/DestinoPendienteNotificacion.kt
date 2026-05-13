package dam2.tfg.psicologiaapp.notificaciones.presentation

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Tipos de destino solicitados desde una notificación. Mantenemos solo los datos imprescindibles
 * para no acoplar el módulo de notificaciones al sistema de navegación principal.
 */
sealed interface DestinoPendienteNotificacion {

    /** Abrir el chat actual del paciente con su psicólogo (o el psicólogo con un paciente concreto). */
    data class Chat(
        val pacienteId: Long,
        val psicologoId: Long,
    ) : DestinoPendienteNotificacion

    /** Abrir la pantalla de tareas del paciente. */
    data object TareasPaciente : DestinoPendienteNotificacion

    /**
     * Abrir la ficha de un paciente concreto en la app del psicólogo. Se usa al pulsar una
     * notificación de alerta clínica (indicios de riesgo detectados por IA).
     */
    data class FichaPaciente(
        val pacienteId: Long,
    ) : DestinoPendienteNotificacion
}

/**
 * Cola de un solo destino pendiente: cuando llega una notificación con la app cerrada o en
 * background, MainActivity la deposita aquí; la primera composición de AppNavHost que esté
 * lista la consume vía [consumirDestinoPendiente] y navega.
 *
 * Consciente de que podemos perder el evento si el usuario reabre la app sin pasar por
 * el splash, este modelo "fire-and-forget" es suficiente para deeplinks tras tap en una
 * notificación, y resiste correctamente cambios de configuración.
 */
object ColaDestinosNotificacion {

    private val _destino = MutableStateFlow<DestinoPendienteNotificacion?>(null)
    val destino: StateFlow<DestinoPendienteNotificacion?> = _destino.asStateFlow()

    fun publicar(nuevo: DestinoPendienteNotificacion) {
        _destino.value = nuevo
    }

    fun consumir(): DestinoPendienteNotificacion? {
        var consumido: DestinoPendienteNotificacion? = null
        _destino.update { actual ->
            consumido = actual
            null
        }
        return consumido
    }

    /**
     * Examina los extras de un Intent recibido y, si vienen de una notificación, los publica
     * como destino pendiente. Devuelve true si había datos válidos.
     */
    fun publicarDesdeIntentSiProcede(intent: Intent?): Boolean {
        intent ?: return false
        val tipo = intent.getStringExtra(ClavesIntentNotificacion.EXTRA_TIPO) ?: return false
        return when (tipo) {
            ClavesIntentNotificacion.TIPO_CHAT -> {
                val pacienteId = intent.getLongExtra(ClavesIntentNotificacion.EXTRA_PACIENTE_ID, 0L)
                val psicologoId = intent.getLongExtra(ClavesIntentNotificacion.EXTRA_PSICOLOGO_ID, 0L)
                publicar(DestinoPendienteNotificacion.Chat(pacienteId, psicologoId))
                true
            }
            ClavesIntentNotificacion.TIPO_TAREA -> {
                publicar(DestinoPendienteNotificacion.TareasPaciente)
                true
            }
            ClavesIntentNotificacion.TIPO_RIESGO -> {
                val pacienteId = intent.getLongExtra(ClavesIntentNotificacion.EXTRA_PACIENTE_ID, 0L)
                if (pacienteId <= 0L) return false
                publicar(DestinoPendienteNotificacion.FichaPaciente(pacienteId))
                true
            }
            else -> false
        }
    }
}
