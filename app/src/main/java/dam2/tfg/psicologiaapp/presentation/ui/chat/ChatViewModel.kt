package dam2.tfg.psicologiaapp.presentation.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam2.tfg.psicologiaapp.chat.domain.usecase.AsegurarChatPacienteUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.AsegurarChatPsicologoUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.EnviarMensajeChatUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.ObservarMensajesChatUseCase
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val asegurarChatPacienteUseCase: AsegurarChatPacienteUseCase,
    private val asegurarChatPsicologoUseCase: AsegurarChatPsicologoUseCase,
    private val observarMensajesChatUseCase: ObservarMensajesChatUseCase,
    private val enviarMensajeChatUseCase: EnviarMensajeChatUseCase,
) : ViewModel() {

    /**
     * When [pacienteId] is 0 the ViewModel operates in PACIENTE mode (calls the
     * patient-side endpoint). A non-zero value activates PSICOLOGO mode.
     */
    private val pacienteId: Long = savedStateHandle.get<Long>(RutasApp.ARG_PACIENTE_ID) ?: 0L

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        val uidActual = firebaseAuth.currentUser?.uid.orEmpty()
        _uiState.update { it.copy(uidActual = uidActual) }
        iniciarChat()
    }

    private fun iniciarChat() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultado = if (pacienteId == 0L) {
                asegurarChatPacienteUseCase()
            } else {
                asegurarChatPsicologoUseCase(pacienteId)
            }

            resultado.fold(
                onSuccess = { chat ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            interlocutorNombre = chat.interlocutorNombre,
                            interlocutorApellidos = chat.interlocutorApellidos,
                            interlocutorFotoPerfilUrl = chat.interlocutorFotoPerfilUrl,
                            rtdbRuta = chat.rtdbRuta,
                        )
                    }
                    observarMensajes(chat.rtdbRuta)
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = e.message ?: "Error al iniciar el chat",
                        )
                    }
                },
            )
        }
    }

    private fun observarMensajes(rtdbRuta: String) {
        viewModelScope.launch {
            observarMensajesChatUseCase(rtdbRuta).collectLatest { mensajes ->
                _uiState.update { it.copy(mensajes = mensajes) }
            }
        }
    }

    fun actualizarTexto(texto: String) {
        _uiState.update { it.copy(textoActual = texto) }
    }

    fun enviarMensaje() {
        val ruta = _uiState.value.rtdbRuta ?: return
        val texto = _uiState.value.textoActual.trim()
        if (texto.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true) }
            enviarMensajeChatUseCase(ruta, texto).fold(
                onSuccess = {
                    _uiState.update { it.copy(textoActual = "", enviando = false) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            enviando = false,
                            mensajeError = e.message ?: "Error al enviar el mensaje",
                        )
                    }
                },
            )
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(mensajeError = null) }
    }

    fun reintentar() {
        iniciarChat()
    }
}
