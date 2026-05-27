package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VerificarExistenciaCorreoUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = true
        val repo = object : FakeUsuarioRepository() {
            override suspend fun existeCorreo(email: String) = Result.success(expected)
        }
        val resultado = VerificarExistenciaCorreoUseCase(repo)(email = "test@test.com")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeUsuarioRepository() {
            override suspend fun existeCorreo(email: String) =
                Result.failure<Boolean>(Exception("Error de prueba"))
        }
        val resultado = VerificarExistenciaCorreoUseCase(repo)(email = "test@test.com")
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
