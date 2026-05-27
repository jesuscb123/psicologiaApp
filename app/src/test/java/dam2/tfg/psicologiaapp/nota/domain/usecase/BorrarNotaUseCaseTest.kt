package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BorrarNotaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeNotaRepository() {
            override suspend fun borrarNota(notaId: Long) = Result.success(expected)
        }
        val useCase = BorrarNotaUseCase(repo)
        val resultado = useCase(notaId = 1L)

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeNotaRepository() {
            override suspend fun borrarNota(notaId: Long) =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = BorrarNotaUseCase(repo)
        val resultado = useCase(notaId = 1L)

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
