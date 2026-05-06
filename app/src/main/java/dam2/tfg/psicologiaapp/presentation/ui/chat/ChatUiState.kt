package dam2.tfg.psicologiaapp.presentation.ui.chat

import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat

data class ChatUiState(
    val cargando: Boolean = true,
    val mensajes: List<MensajeChat> = emptyList(),
    val textoActual: String = "",
    val enviando: Boolean = false,
    val interlocutorNombre: String = "",
    val interlocutorApellidos: String = "",
    val interlocutorFotoPerfilUrl: String? = null,
    val uidActual: String = "",
    val mensajeError: String? = null,
    val rtdbRuta: String? = null,
    val chatId: String? = null,
)
