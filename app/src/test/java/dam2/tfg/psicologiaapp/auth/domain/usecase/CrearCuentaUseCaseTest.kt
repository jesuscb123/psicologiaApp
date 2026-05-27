package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrearCuentaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = "uid-123"
        val repo = object : FakeAuthRepository() {
            override suspend fun crearCuenta(correo: String, contrasena: String) = Result.success(expected)
        }
        val useCase = CrearCuentaUseCase(repo)
        val resultado = useCase(correo = "a@b.com", contrasena = "pass")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeAuthRepository() {
            override suspend fun crearCuenta(correo: String, contrasena: String) =
                Result.failure<String>(Exception("Error de prueba"))
        }
        val useCase = CrearCuentaUseCase(repo)
        val resultado = useCase(correo = "a@b.com", contrasena = "pass")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
