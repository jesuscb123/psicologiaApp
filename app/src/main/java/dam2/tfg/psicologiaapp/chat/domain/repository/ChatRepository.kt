package dam2.tfg.psicologiaapp.chat.domain.repository

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    /**
     * Calls the backend to validate that the current patient has an assigned psychologist
     * and returns the chat session data including the RTDB path.
     */
    suspend fun asegurarChatPaciente(): Result<Chat>

    /**
     * Calls the backend to validate that [pacienteId] belongs to the current psychologist's
     * patient list and returns the chat session data including the RTDB path.
     */
    suspend fun asegurarChatPsicologo(pacienteId: Long): Result<Chat>

    /**
     * Returns a [Flow] that emits the full list of messages every time a new message arrives
     * in the Firebase Realtime Database node at [rtdbRuta].
     */
    fun observarMensajes(rtdbRuta: String): Flow<List<MensajeChat>>

    /**
     * Pushes a new message under [rtdbRuta]/mensajes. The sender UID is obtained from the
     * current Firebase Auth session, so the caller only needs to supply the text.
     */
    suspend fun enviarMensaje(rtdbRuta: String, texto: String): Result<Unit>
}
