package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarcarChatLeidoUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeChatRepository() {
            override suspend fun marcarChatLeido(rtdbRuta: String) = Result.success(expected)
        }
        val useCase = MarcarChatLeidoUseCase(repo)
        val resultado = useCase(rtdbRuta = "ruta")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeChatRepository() {
            override suspend fun marcarChatLeido(rtdbRuta: String) =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = MarcarChatLeidoUseCase(repo)
        val resultado = useCase(rtdbRuta = "ruta")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
