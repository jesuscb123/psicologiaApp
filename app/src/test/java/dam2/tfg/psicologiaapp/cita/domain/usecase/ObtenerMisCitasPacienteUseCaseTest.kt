package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaPersistido
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObtenerMisCitasPacienteUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = listOf(Cita(1L, "2024-01-01T10:00:00+01:00", "2024-01-01T11:00:00+01:00", 1L, 2L, "Dr", "Pac", EstadoCitaPersistido.RESERVADA, EstadoCitaCalculado.ACTIVA))
        val repo = object : FakeCitaRepository() {
            override suspend fun getMisCitasPaciente() = Result.success(expected)
        }
        val useCase = ObtenerMisCitasPacienteUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeCitaRepository() {
            override suspend fun getMisCitasPaciente() =
                Result.failure<List<Cita>>(Exception("Error de prueba"))
        }
        val useCase = ObtenerMisCitasPacienteUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
