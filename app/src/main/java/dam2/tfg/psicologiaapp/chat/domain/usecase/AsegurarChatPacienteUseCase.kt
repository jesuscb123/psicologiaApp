package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import javax.inject.Inject

class AsegurarChatPacienteUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(): Result<Chat> = chatRepository.asegurarChatPaciente()
}
