package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakeChatRepository : ChatRepository {
    override suspend fun asegurarChatPaciente(): Result<Chat> = Result.failure(NotImplementedError())
    override suspend fun asegurarChatPsicologo(pacienteId: Long): Result<Chat> = Result.failure(NotImplementedError())
    override fun observarMensajes(rtdbRuta: String): Flow<List<MensajeChat>> = emptyFlow()
    override suspend fun enviarMensaje(rtdbRuta: String, texto: String): Result<Unit> = Result.success(Unit)
    override suspend fun marcarChatLeido(rtdbRuta: String): Result<Unit> = Result.success(Unit)
    override fun observarNoLeidosChatIds(miUid: String): Flow<Set<String>> = emptyFlow()
}
