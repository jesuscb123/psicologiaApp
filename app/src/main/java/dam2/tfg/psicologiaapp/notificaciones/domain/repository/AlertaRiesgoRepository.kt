package dam2.tfg.psicologiaapp.notificaciones.domain.repository

import kotlinx.coroutines.flow.Flow

interface AlertaRiesgoRepository {

    /**
     * Observes the RTDB `alertasRiesgo/{psicologoUid}` node and emits the set of
     * pacienteIds that currently have an active (unacknowledged) risk alert.
     */
    fun observarAlertasRiesgo(psicologoUid: String): Flow<Set<Long>>

    /**
     * Writes a risk alert for [pacienteId] under the psychologist identified by [psicologoUid].
     * Called from the FCM service when a TIPO_RIESGO push is received.
     */
    suspend fun marcarAlertaRiesgo(psicologoUid: String, pacienteId: Long)

    /**
     * Removes the risk alert for [pacienteId] from the psychologist's RTDB node.
     * Called when the psychologist opens the patient's file screen.
     */
    suspend fun limpiarAlertaRiesgo(psicologoUid: String, pacienteId: Long)
}
