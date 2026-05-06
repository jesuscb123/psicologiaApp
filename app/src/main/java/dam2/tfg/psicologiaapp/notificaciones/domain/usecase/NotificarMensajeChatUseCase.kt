package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository
import javax.inject.Inject

/**
 * Solicita al backend que envíe un push al destinatario del chat. La validación
 * (que el usuario participa en el chat) la hace el backend.
 */
class NotificarMensajeChatUseCase @Inject constructor(
    private val notificacionesRepository: NotificacionesRepository,
) {
    suspend operator fun invoke(chatId: String, vistaPreviaTexto: String): Result<Unit> =
        notificacionesRepository.notificarMensajeChat(
            chatId = chatId,
            vistaPreviaTexto = vistaPreviaTexto.take(MAX_PREVIEW),
        )

    private companion object {
        const val MAX_PREVIEW = 140
    }
}
