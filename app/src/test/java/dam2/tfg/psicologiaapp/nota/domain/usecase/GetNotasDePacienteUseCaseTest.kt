package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNotasDePacienteUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = listOf(Nota(1L, "Asunto", "Desc", "2024-01-01", 1L, 2L))
        val repo = object : FakeNotaRepository() {
            override suspend fun getNotasDePaciente(pacienteId: Long) = Result.success(expected)
        }
        val useCase = GetNotasDePacienteUseCase(repo)
        val resultado = useCase(pacienteId = 1L)

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeNotaRepository() {
            override suspend fun getNotasDePaciente(pacienteId: Long) =
                Result.failure<List<Nota>>(Exception("Error de prueba"))
        }
        val useCase = GetNotasDePacienteUseCase(repo)
        val resultado = useCase(pacienteId = 1L)

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
