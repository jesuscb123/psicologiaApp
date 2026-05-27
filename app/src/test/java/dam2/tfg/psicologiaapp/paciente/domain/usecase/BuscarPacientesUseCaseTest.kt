package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.test.fakes.FakePacienteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuscarPacientesUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = listOf(Paciente(1L, "uid", "Ana", "Lopez", null, 2L, 10L))
        val repo = object : FakePacienteRepository() {
            override suspend fun buscarPacientes(nombreUsuario: String) = Result.success(expected)
        }
        val resultado = BuscarPacientesUseCase(repo)(nombreUsuario = "Ana")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePacienteRepository() {
            override suspend fun buscarPacientes(nombreUsuario: String) =
                Result.failure<List<Paciente>>(Exception("Error de prueba"))
        }
        val resultado = BuscarPacientesUseCase(repo)(nombreUsuario = "Ana")
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
