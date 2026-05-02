package dam2.tfg.psicologiaapp.chat.domain.model

data class Chat(
    val chatId: String,
    val interlocutorNombre: String,
    val interlocutorApellidos: String,
    val interlocutorFotoPerfilUrl: String?,
    val rtdbRuta: String,
)
