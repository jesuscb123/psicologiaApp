package dam2.tfg.psicologiaapp.chat.domain.model

data class MensajeChat(
    val id: String,
    val texto: String,
    val remitenteUid: String,
    val enviadoEn: Long,
)
