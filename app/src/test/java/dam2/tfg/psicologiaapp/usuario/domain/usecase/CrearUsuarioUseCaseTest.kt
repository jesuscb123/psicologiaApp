package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioSinRol
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrearUsuarioUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val request = PacienteRequest("Nombre", "Apellidos", null, psicologoId = 2L)
        val expected = UsuarioSinRol(1L, "uid", "Nombre", "Apellidos", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioRepository() {
            override suspend fun crearUsuario(request: dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest): Result<dam2.tfg.psicologiaapp.usuario.domain.model.Usuario> =
                Result.success(expected)
        }
        val resultado = CrearUsuarioUseCase(repo)(request)
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val request = PacienteRequest("Nombre", "Apellidos", null, psicologoId = 2L)
        val repo = object : FakeUsuarioRepository() {
            override suspend fun crearUsuario(request: dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest): Result<dam2.tfg.psicologiaapp.usuario.domain.model.Usuario> =
                Result.failure<dam2.tfg.psicologiaapp.usuario.domain.model.Usuario>(Exception("Error de prueba"))
        }
        val resultado = CrearUsuarioUseCase(repo)(request)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
