package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarNoLeidosEnChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(miUid: String): Flow<Set<String>> =
        chatRepository.observarNoLeidosChatIds(miUid)
}
