package dam2.tfg.psicologiaapp.notificaciones.data.remote

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG_RIESGO = "AlertaRiesgoFirebase"
private const val NODO_ALERTAS_RIESGO = "alertasRiesgo"

@Singleton
class AlertaRiesgoFuenteDatosFirebase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) {

    /**
     * Observes `alertasRiesgo/{psicologoUid}` and emits the set of pacienteIds with active alerts.
     */
    fun observarAlertasRiesgo(psicologoUid: String): Flow<Set<Long>> = callbackFlow {
        if (psicologoUid.isBlank()) {
            trySend(emptySet())
            awaitClose()
            return@callbackFlow
        }
        val ref = firebaseDatabase.getReference("$NODO_ALERTAS_RIESGO/$psicologoUid")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pacienteIds = snapshot.children
                    .mapNotNull { it.key?.toLongOrNull() }
                    .toSet()
                trySend(pacienteIds)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG_RIESGO, "observarAlertasRiesgo cancelado: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun marcarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {
        firebaseDatabase
            .getReference("$NODO_ALERTAS_RIESGO/$psicologoUid/$pacienteId")
            .setValue(System.currentTimeMillis())
            .await()
    }

    suspend fun limpiarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {
        firebaseDatabase
            .getReference("$NODO_ALERTAS_RIESGO/$psicologoUid/$pacienteId")
            .removeValue()
            .await()
    }
}
