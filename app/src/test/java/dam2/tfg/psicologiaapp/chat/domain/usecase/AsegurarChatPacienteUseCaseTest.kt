package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsegurarChatPacienteUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Chat("c1", "Ana", "Lopez", null, "ruta")
        val repo = object : FakeChatRepository() {
            override suspend fun asegurarChatPaciente() = Result.success(expected)
        }
        val useCase = AsegurarChatPacienteUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeChatRepository() {
            override suspend fun asegurarChatPaciente() =
                Result.failure<Chat>(Exception("Error de prueba"))
        }
        val useCase = AsegurarChatPacienteUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
