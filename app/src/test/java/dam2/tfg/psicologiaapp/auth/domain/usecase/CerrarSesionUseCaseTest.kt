package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CerrarSesionUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeAuthRepository() {
            override suspend fun cerrarSesion() = Result.success(expected)
        }
        val useCase = CerrarSesionUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeAuthRepository() {
            override suspend fun cerrarSesion() =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val useCase = CerrarSesionUseCase(repo)
        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
