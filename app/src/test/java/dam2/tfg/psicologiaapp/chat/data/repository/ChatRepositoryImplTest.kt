package dam2.tfg.psicologiaapp.chat.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.chat.data.remote.ChatApi
import dam2.tfg.psicologiaapp.chat.data.remote.ChatFuenteDatosFirebase
import dam2.tfg.psicologiaapp.chat.data.remote.ChatResponseDto
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

class ChatRepositoryImplTest {

    private val api = FakeChatApi()
    private val chatFirebase = mock<ChatFuenteDatosFirebase>()
    private val proveedorToken = mock<ProveedorTokenFirebase>()
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setUp() {
        repository = ChatRepositoryImpl(api, chatFirebase, proveedorToken)
    }

    @Test
    fun `asegurarChatPaciente debe mapear respuesta exitosa`() = runTest {
        api.respuestaPaciente = Response.success(chatDto)

        val resultado = repository.asegurarChatPaciente()

        assertTrue(resultado.isSuccess)
        assertEquals("chat-1", resultado.getOrNull()?.chatId)
        assertEquals("chats/chat-1", resultado.getOrNull()?.rtdbRuta)
    }

    @Test
    fun `asegurarChatPaciente debe reintentar tras 401`() = runTest {
        api.respuestaPacienteSecuencia = listOf(
            Response.error(401, "".toResponseBody(null)),
            Response.success(chatDto),
        )
        whenever(proveedorToken.obtenerToken(forzarRenovacion = true)).thenReturn("token")

        val resultado = repository.asegurarChatPaciente()

        assertTrue(resultado.isSuccess)
        verify(proveedorToken).obtenerToken(forzarRenovacion = true)
        assertEquals(2, api.llamadasPaciente)
    }

    @Test
    fun `asegurarChatPaciente debe propagar error 403`() = runTest {
        api.respuestaPaciente = Response.error(403, "".toResponseBody(null))

        val resultado = repository.asegurarChatPaciente()

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("HTTP 403") == true)
    }

    @Test
    fun `asegurarChatPsicologo debe mapear respuesta exitosa`() = runTest {
        api.respuestaPsicologo = Response.success(chatDto)

        val resultado = repository.asegurarChatPsicologo(100L)

        assertTrue(resultado.isSuccess)
        assertEquals(100L, api.ultimoPacienteId)
    }

    @Test
    fun `enviarMensaje debe completar cuando Firebase tiene exito`() = runTest {
        whenever(chatFirebase.enviarMensaje("chats/chat-1", "Hola")).then { }

        val resultado = repository.enviarMensaje("chats/chat-1", "Hola")

        assertTrue(resultado.isSuccess)
        verify(chatFirebase).enviarMensaje("chats/chat-1", "Hola")
    }

    @Test
    fun `enviarMensaje debe propagar error de Firebase`() = runTest {
        whenever(chatFirebase.enviarMensaje("chats/chat-1", "Hola"))
            .thenThrow(IllegalStateException("Sin auth"))

        val resultado = repository.enviarMensaje("chats/chat-1", "Hola")

        assertTrue(resultado.isFailure)
        assertEquals("Sin auth", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `marcarChatLeido debe completar cuando Firebase tiene exito`() = runTest {
        whenever(chatFirebase.marcarLeido("chats/chat-1")).then { }

        val resultado = repository.marcarChatLeido("chats/chat-1")

        assertTrue(resultado.isSuccess)
    }

    @Test
    fun `observarMensajes debe delegar en Firebase`() = runTest {
        val mensajes = listOf(
            MensajeChat("m1", "Hola", "uid-1", 1000L),
        )
        whenever(chatFirebase.observarMensajes("chats/chat-1")).thenReturn(flowOf(mensajes))

        repository.observarMensajes("chats/chat-1").test {
            assertEquals(mensajes, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `observarNoLeidosChatIds debe delegar en Firebase`() = runTest {
        whenever(chatFirebase.observarNoLeidosChatIds("uid-1")).thenReturn(flowOf(setOf("chat-1")))

        repository.observarNoLeidosChatIds("uid-1").test {
            assertEquals(setOf("chat-1"), awaitItem())
            awaitComplete()
        }
    }

    private val chatDto = ChatResponseDto(
        chatId = "chat-1",
        interlocutorNombre = "Ana",
        interlocutorApellidos = "López",
        interlocutorFotoPerfilUrl = null,
        rtdbRuta = "chats/chat-1",
    )

    private class FakeChatApi : ChatApi {
        var respuestaPaciente: Response<ChatResponseDto> = Response.success(
            ChatResponseDto("", "", "", null, ""),
        )
        var respuestaPsicologo: Response<ChatResponseDto> = respuestaPaciente
        var respuestaPacienteSecuencia: List<Response<ChatResponseDto>>? = null
        var ultimoPacienteId: Long? = null
        var llamadasPaciente = 0

        override suspend fun asegurarChatPaciente(): Response<ChatResponseDto> {
            llamadasPaciente++
            return respuestaPacienteSecuencia?.getOrNull(llamadasPaciente - 1) ?: respuestaPaciente
        }

        override suspend fun asegurarChatPsicologo(pacienteId: Long): Response<ChatResponseDto> {
            ultimoPacienteId = pacienteId
            return respuestaPsicologo
        }
    }
}
