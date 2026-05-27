package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnviarMensajeChatUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeChatRepository() {
            override suspend fun enviarMensaje(rtdbRuta: String, texto: String) = Result.success(expected)
        }
        val useCase = EnviarMensajeChatUseCase(repo)
        val resultado = useCase(rtdbRuta = "ruta", texto = "hola")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeChatRepository() {
            override suspend fun enviarMensaje(rtdbRuta: String, texto: String) =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = EnviarMensajeChatUseCase(repo)
        val resultado = useCase(rtdbRuta = "ruta", texto = "hola")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
