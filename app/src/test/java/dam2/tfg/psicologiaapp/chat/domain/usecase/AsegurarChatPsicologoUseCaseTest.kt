package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsegurarChatPsicologoUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Chat("c1", "Ana", "Lopez", null, "ruta")
        val repo = object : FakeChatRepository() {
            override suspend fun asegurarChatPsicologo(pacienteId: Long) = Result.success(expected)
        }
        val useCase = AsegurarChatPsicologoUseCase(repo)
        val resultado = useCase(pacienteId = 1L)

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeChatRepository() {
            override suspend fun asegurarChatPsicologo(pacienteId: Long) =
                Result.failure<Chat>(Exception("Error de prueba"))
        }
        val useCase = AsegurarChatPsicologoUseCase(repo)
        val resultado = useCase(pacienteId = 1L)

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
