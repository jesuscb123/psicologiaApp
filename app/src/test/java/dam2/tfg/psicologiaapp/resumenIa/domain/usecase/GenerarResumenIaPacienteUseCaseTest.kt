package dam2.tfg.psicologiaapp.resumenIa.domain.usecase

import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa
import dam2.tfg.psicologiaapp.test.fakes.FakeResumenIaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerarResumenIaPacienteUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = ResumenIa("resumen", 3, "2024-01-01", "modelo")
        val repo = object : FakeResumenIaRepository() {
            override suspend fun generarResumenNotasPaciente(pacienteId: Long) = Result.success(expected)
        }
        val useCase = GenerarResumenIaPacienteUseCase(repo)
        val resultado = useCase(pacienteId = 1L)

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeResumenIaRepository() {
            override suspend fun generarResumenNotasPaciente(pacienteId: Long) =
                Result.failure<ResumenIa>(Exception("Error de prueba"))
        }
        val useCase = GenerarResumenIaPacienteUseCase(repo)
        val resultado = useCase(pacienteId = 1L)

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
