package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPacientesDePsicologoUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = listOf(Paciente(1L, "uid", "Ana", "Lopez", null, 2L, 10L))
        val repo = object : FakePsicologoRepository() {
            override suspend fun getPacientesDePsicologo() = Result.success(expected)
        }
        val resultado = GetPacientesDePsicologoUseCase(repo)()
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePsicologoRepository() {
            override suspend fun getPacientesDePsicologo() =
                Result.failure<List<Paciente>>(Exception("Error de prueba"))
        }
        val resultado = GetPacientesDePsicologoUseCase(repo)()
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
