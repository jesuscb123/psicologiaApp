package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SincronizarMisCitasPacienteUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeCitaRepository() {
            override suspend fun sincronizarMisCitasPaciente() = Result.success(expected)
        }
        val useCase = SincronizarMisCitasPacienteUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeCitaRepository() {
            override suspend fun sincronizarMisCitasPaciente() =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = SincronizarMisCitasPacienteUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
