package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import javax.inject.Inject

class AsegurarChatPsicologoUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(pacienteId: Long): Result<Chat> =
        chatRepository.asegurarChatPsicologo(pacienteId)
}
