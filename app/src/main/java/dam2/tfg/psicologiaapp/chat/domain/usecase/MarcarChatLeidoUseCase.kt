package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import javax.inject.Inject

class MarcarChatLeidoUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(rtdbRuta: String): Result<Unit> =
        chatRepository.marcarChatLeido(rtdbRuta)
}
