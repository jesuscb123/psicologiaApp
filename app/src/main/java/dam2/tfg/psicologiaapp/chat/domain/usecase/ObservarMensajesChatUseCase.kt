package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarMensajesChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(rtdbRuta: String): Flow<List<MensajeChat>> =
        chatRepository.observarMensajes(rtdbRuta)
}
