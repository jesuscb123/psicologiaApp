package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrearNotaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Nota(1L, "Asunto", "Desc", "2024-01-01", 1L, 2L)
        val repo = object : FakeNotaRepository() {
            override suspend fun crearNota(asunto: String, descripcion: String) = Result.success(expected)
        }
        val useCase = CrearNotaUseCase(repo)
        val resultado = useCase(asunto = "A", descripcion = "D")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeNotaRepository() {
            override suspend fun crearNota(asunto: String, descripcion: String) =
                Result.failure<Nota>(Exception("Error de prueba"))
        }
        val useCase = CrearNotaUseCase(repo)
        val resultado = useCase(asunto = "A", descripcion = "D")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
