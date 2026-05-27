package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SincronizarFotoPerfilUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = UsuarioPerfilBasico(1L, "uid", "Nombre", "Apellidos", "email@test.com", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioRepository() {
            override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil> =
                Result.success(expected)
        }
        val resultado = SincronizarFotoPerfilUseCase(repo)(byteArrayOf(1, 2), "image/png")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeUsuarioRepository() {
            override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil> =
                Result.failure<dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil>(Exception("Error de prueba"))
        }
        val resultado = SincronizarFotoPerfilUseCase(repo)(byteArrayOf(1, 2), null)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
