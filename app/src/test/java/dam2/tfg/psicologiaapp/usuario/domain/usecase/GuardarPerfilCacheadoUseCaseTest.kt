package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardarPerfilCacheadoUseCaseTest {

    @Test
    fun `invoke debe delegar en el repositorio de cache`() = runTest {
        var guardado = false
        val perfil = UsuarioPerfilBasico(1L, "uid", "Nombre", "Apellidos", "email@test.com", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioCacheRepository() {
            override suspend fun guardarDesdePerfil(perfil: dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil) {
                guardado = true
            }
        }
        GuardarPerfilCacheadoUseCase(repo)(perfil)
        assertTrue(guardado)
    }

    @Test
    fun `invoke debe propagar excepcion del repositorio`() = runTest {
        val perfil = UsuarioPerfilBasico(1L, "uid", "Nombre", "Apellidos", "email@test.com", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioCacheRepository() {
            override suspend fun guardarDesdePerfil(perfil: dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil) {
                throw Exception("Error de prueba")
            }
        }
        try {
            GuardarPerfilCacheadoUseCase(repo)(perfil)
            assertTrue(false)
        } catch (e: Exception) {
            assertTrue(e.message == "Error de prueba")
        }
    }
}
