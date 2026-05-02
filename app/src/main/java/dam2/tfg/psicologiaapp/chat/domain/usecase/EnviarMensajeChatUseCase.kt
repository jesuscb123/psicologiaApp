package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import javax.inject.Inject

class EnviarMensajeChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(rtdbRuta: String, texto: String): Result<Unit> =
        chatRepository.enviarMensaje(rtdbRuta, texto)
}
