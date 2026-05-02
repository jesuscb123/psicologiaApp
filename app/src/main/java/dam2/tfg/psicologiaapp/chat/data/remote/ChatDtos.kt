package dam2.tfg.psicologiaapp.chat.data.remote

import dam2.tfg.psicologiaapp.chat.domain.model.Chat

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
    interlocutorFotoPerfilUrl = interlocutorFotoPerfilUrl,
    rtdbRuta = rtdbRuta,
)
