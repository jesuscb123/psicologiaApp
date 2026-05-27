package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.test.fakes.FakePacienteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsignarPsicologoUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Paciente(1L, "uid", "Ana", "Lopez", null, 2L, 10L)
        val repo = object : FakePacienteRepository() {
            override suspend fun asignarPsicologo(psicologoId: Long) = Result.success(expected)
        }
        val resultado = AsignarPsicologoUseCase(repo)(psicologoId = 2L)
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePacienteRepository() {
            override suspend fun asignarPsicologo(psicologoId: Long) =
                Result.failure<Paciente>(Exception("Error de prueba"))
        }
        val resultado = AsignarPsicologoUseCase(repo)(psicologoId = 2L)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
