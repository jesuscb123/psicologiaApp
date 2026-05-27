package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SolicitarRestablecerContrasenaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeAuthRepository() {
            override suspend fun solicitarRestablecerContrasena(correo: String) = Result.success(expected)
        }
        val useCase = SolicitarRestablecerContrasenaUseCase(repo)
        val resultado = useCase(correo = "a@b.com")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeAuthRepository() {
            override suspend fun solicitarRestablecerContrasena(correo: String) =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = SolicitarRestablecerContrasenaUseCase(repo)
        val resultado = useCase(correo = "a@b.com")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
