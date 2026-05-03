package dam2.tfg.psicologiaapp.chat.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dam2.tfg.psicologiaapp.BuildConfig
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG_CHAT_RTD = "ChatFirebase"

@Singleton
class ChatFuenteDatosFirebase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
) {

    /**
     * Opens a realtime listener on [rtdbRuta]/mensajes and emits an updated sorted list
     * every time a message is added, changed, or removed.
     */
    fun observarMensajes(rtdbRuta: String): Flow<List<MensajeChat>> = callbackFlow {
        // Un solo hilo: los callbacks de RTDB pueden llegar antes de que el canal tenga receptor;
        // trySend falla y se perdían listas (p. ej. el primer snapshot o el mensaje recién escrito).
        val despachoOrdenado = Dispatchers.Default.limitedParallelism(1)
        val ref = firebaseDatabase.getReference("$rtdbRuta/mensajes")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mensajes = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val texto =
                        child.child("texto").getValue(String::class.java)?.trim()?.takeIf { it.isNotEmpty() }
                            ?: child.child("texto").value?.toString()?.trim()?.takeIf { it.isNotEmpty() }
                            ?: return@mapNotNull null
                    val remitenteUidRaw = child.child("remitenteUid").value ?: return@mapNotNull null
                    val remitenteUid = remitenteUidRaw.toString().trim().takeIf { it.isNotEmpty() }
                        ?: return@mapNotNull null
                    val enviadoEn = tiempoEnMilisegundosDesdeRtdb(child.child("enviadoEn"))
                        ?: return@mapNotNull null
                    MensajeChat(
                        id = id,
                        texto = texto,
                        remitenteUid = remitenteUid,
                        enviadoEn = enviadoEn,
                    )
                }.sortedBy { it.enviadoEn }
                launch(despachoOrdenado) {
                    try {
                        send(mensajes)
                    } catch (_: ClosedSendChannelException) {
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG_CHAT_RTD,
                    "Escucha cancelada (${ref.path}): código=${error.code} mensaje=${error.message} detalle=${error.details}",
                )
                close(error.toException())
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG_CHAT_RTD, "Observando ${ref.path}")
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Escribe el mensaje y espera al Task para que errores de reglas/App Check/host aparezcan
     * como fallo recoverable por el repositorio (no escritura silenciosa).
     */
    suspend fun enviarMensaje(rtdbRuta: String, texto: String) {
        val uid = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("No hay usuario autenticado en Firebase")

        val ref = firebaseDatabase.getReference("$rtdbRuta/mensajes").push()
        val mensaje = mapOf(
            "texto" to texto,
            "remitenteUid" to uid,
            "enviadoEn" to System.currentTimeMillis(),
        )
        try {
            withTimeout(25_000L) {
                ref.setValue(mensaje).await()
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG_CHAT_RTD, "Timeout setValue en ${ref.path}")
            throw IllegalStateException(
                "Timeout al enviar: revisa que la URL de RTDB sea la misma que en el backend y la conexión.",
                e,
            )
        }
    }
}

/**
 * Firebase Realtime Database deserializa valores numéricos como [Double] con frecuencia.
 * Obtener solo Long mediante getValue desemboca en null; entonces cada mensaje se perdía en
 * el mapNotNull y la lista quedaba vacía.
 */
private fun tiempoEnMilisegundosDesdeRtdb(nodo: DataSnapshot): Long? =
    milisegundosDesdeValorRtdb(nodo.value)

private fun milisegundosDesdeValorRtdb(valorRaw: Any?): Long? {
    val valor = valorRaw ?: return null
    return when (valor) {
        is Long -> valor
        is Int -> valor.toLong()
        is Double -> valor.toLong()
        is Float -> valor.toLong()
        is Number -> valor.toLong()
        is String -> valor.toDoubleOrNull()?.toLong()
        else -> null
    }
}
