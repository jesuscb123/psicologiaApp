package dam2.tfg.psicologiaapp.chat.data.remote

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.paciente.data.mappers.normalizarUrlFotoPerfilCliente

data class ChatResponseDto(
    val chatId: String,
    val interlocutorNombre: String,
    val interlocutorApellidos: String,
    val interlocutorFotoPerfilUrl: String?,
    val rtdbRuta: String,
)

fun ChatResponseDto.toDomain() = Chat(
    chatId = chatId,
    interlocutorNombre = interlocutorNombre,
    interlocutorApellidos = interlocutorApellidos,
    interlocutorFotoPerfilUrl = normalizarUrlFotoPerfilCliente(interlocutorFotoPerfilUrl),
    rtdbRuta = rtdbRuta,
)
