package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeNotificacionesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificarMensajeChatUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeNotificacionesRepository() {
            override suspend fun notificarMensajeChat(chatId: String, vistaPreviaTexto: String) = Result.success(expected)
        }
        val useCase = NotificarMensajeChatUseCase(repo)
        val resultado = useCase(chatId = "c1", vistaPreviaTexto = "hola")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeNotificacionesRepository() {
            override suspend fun notificarMensajeChat(chatId: String, vistaPreviaTexto: String) =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = NotificarMensajeChatUseCase(repo)
        val resultado = useCase(chatId = "c1", vistaPreviaTexto = "hola")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
