package dam2.tfg.psicologiaapp.chat.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

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
        val ref = firebaseDatabase.getReference("$rtdbRuta/mensajes")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mensajes = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val texto = child.child("texto").getValue(String::class.java)
                        ?: return@mapNotNull null
                    val remitenteUid = child.child("remitenteUid").getValue(String::class.java)
                        ?: return@mapNotNull null
                    val enviadoEn = child.child("enviadoEn").getValue(Long::class.java)
                        ?: return@mapNotNull null
                    MensajeChat(
                        id = id,
                        texto = texto,
                        remitenteUid = remitenteUid,
                        enviadoEn = enviadoEn,
                    )
                }.sortedBy { it.enviadoEn }
                trySend(mensajes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Pushes a new message to [rtdbRuta]/mensajes. Throws if no user is signed in.
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
        ref.setValue(mensaje).await()
    }
}
