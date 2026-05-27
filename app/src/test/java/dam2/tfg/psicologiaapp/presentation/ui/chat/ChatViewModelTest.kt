package dam2.tfg.psicologiaapp.presentation.ui.chat

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.chat.domain.usecase.AsegurarChatPacienteUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.AsegurarChatPsicologoUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.EnviarMensajeChatUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.MarcarChatLeidoUseCase
import dam2.tfg.psicologiaapp.chat.domain.usecase.ObservarMensajesChatUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.NotificarMensajeChatUseCase
import dam2.tfg.psicologiaapp.presentation.navegacion.RutasApp
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotificacionesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val chatEjemplo = Chat(
        chatId = "paciente_3_psicologo_2",
        interlocutorNombre = "Ana",
        interlocutorApellidos = "López",
        interlocutorFotoPerfilUrl = null,
        rtdbRuta = "chats/test",
    )

    @Test
    fun init_modoPaciente_cargaChat() = runTest {
        val viewModel = crearViewModel(
            pacienteId = 0L,
            asegurarPaciente = { Result.success(chatEjemplo) },
        )
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals("Ana", estado.interlocutorNombre)
        assertEquals("chats/test", estado.rtdbRuta)
    }

    @Test
    fun init_falloAsegurarChat_muestraError() = runTest {
        val viewModel = crearViewModel(
            pacienteId = 0L,
            asegurarPaciente = { Result.failure(Exception("Sin psicólogo")) },
        )
        advanceUntilIdle()

        assertEquals("Sin psicólogo", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun actualizarTexto_actualizaEstado() = runTest {
        val viewModel = crearViewModel(pacienteId = 0L, asegurarPaciente = { Result.success(chatEjemplo) })
        advanceUntilIdle()

        viewModel.actualizarTexto("Hola")

        assertEquals("Hola", viewModel.uiState.value.textoActual)
    }

    @Test
    fun enviarMensaje_exito_limpiaTexto() = runTest {
        val mensajesFlow = MutableStateFlow<List<MensajeChat>>(emptyList())
        val viewModel = crearViewModel(
            pacienteId = 0L,
            asegurarPaciente = { Result.success(chatEjemplo) },
            mensajesFlow = mensajesFlow,
        )
        advanceUntilIdle()

        viewModel.actualizarTexto("Hola mundo")
        viewModel.enviarMensaje()
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.textoActual)
        assertFalse(viewModel.uiState.value.enviando)
    }

    @Test
    fun enviarMensaje_textoVacio_noHaceNada() = runTest {
        val viewModel = crearViewModel(pacienteId = 0L, asegurarPaciente = { Result.success(chatEjemplo) })
        advanceUntilIdle()

        viewModel.enviarMensaje()

        assertEquals("", viewModel.uiState.value.textoActual)
    }

    @Test
    fun limpiarError_borraMensaje() = runTest {
        val viewModel = crearViewModel(
            pacienteId = 0L,
            asegurarPaciente = { Result.failure(Exception("Error")) },
        )
        advanceUntilIdle()

        viewModel.limpiarError()

        assertNull(viewModel.uiState.value.mensajeError)
    }

    @Test
    fun observarMensajes_actualizaLista() = runTest {
        val mensajesFlow = MutableStateFlow<List<MensajeChat>>(emptyList())
        val viewModel = crearViewModel(
            pacienteId = 0L,
            asegurarPaciente = { Result.success(chatEjemplo) },
            mensajesFlow = mensajesFlow,
        )
        advanceUntilIdle()

        mensajesFlow.value = listOf(MensajeChat("1", "Hola", "uid-1", 1000L))
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.mensajes.size)
        assertEquals("Hola", viewModel.uiState.value.mensajes.first().texto)
    }

    private fun crearViewModel(
        pacienteId: Long,
        asegurarPaciente: suspend () -> Result<Chat> = { Result.failure(NotImplementedError()) },
        asegurarPsicologo: suspend (Long) -> Result<Chat> = { Result.failure(NotImplementedError()) },
        mensajesFlow: MutableStateFlow<List<MensajeChat>> = MutableStateFlow(emptyList()),
    ): ChatViewModel {
        val repo = object : FakeChatRepository() {
            override suspend fun asegurarChatPaciente() = asegurarPaciente()
            override suspend fun asegurarChatPsicologo(pacienteId: Long) = asegurarPsicologo(pacienteId)
            override fun observarMensajes(rtdbRuta: String) = mensajesFlow
        }
        val user = mock<FirebaseUser>()
        whenever(user.uid).thenReturn("uid-test")
        val firebaseAuth = mock<FirebaseAuth>()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        val savedState = SavedStateHandle(mapOf(RutasApp.ARG_PACIENTE_ID to pacienteId))
        return ChatViewModel(
            savedStateHandle = savedState,
            firebaseAuth = firebaseAuth,
            asegurarChatPacienteUseCase = AsegurarChatPacienteUseCase(repo),
            asegurarChatPsicologoUseCase = AsegurarChatPsicologoUseCase(repo),
            observarMensajesChatUseCase = ObservarMensajesChatUseCase(repo),
            enviarMensajeChatUseCase = EnviarMensajeChatUseCase(repo),
            notificarMensajeChatUseCase = NotificarMensajeChatUseCase(FakeNotificacionesRepository()),
            marcarChatLeidoUseCase = MarcarChatLeidoUseCase(repo),
        )
    }
}
