package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuscarPsicologosUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = listOf(Psicologo(1L, 10L, "uid", "Dr", "House", null, "12345", listOf("Clinica"), "Desc"))
        val repo = object : FakePsicologoRepository() {
            override suspend fun buscarPsicologos(nombreUsuario: String) = Result.success(expected)
        }
        val resultado = BuscarPsicologosUseCase(repo)(nombreUsuario = "Dr")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePsicologoRepository() {
            override suspend fun buscarPsicologos(nombreUsuario: String) =
                Result.failure<List<Psicologo>>(Exception("Error de prueba"))
        }
        val resultado = BuscarPsicologosUseCase(repo)(nombreUsuario = "Dr")
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
