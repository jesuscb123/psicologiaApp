package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class LimpiarPerfilCacheadoUseCaseTest {

    @Test
    fun `invoke debe delegar en el repositorio de cache`() = runTest {
        var limpiado = false
        val repo = object : FakeUsuarioCacheRepository() {
            override suspend fun limpiarCache() {
                limpiado = true
            }
        }
        LimpiarPerfilCacheadoUseCase(repo)()
        assertTrue(limpiado)
    }

    @Test
    fun `invoke debe propagar excepcion del repositorio`() = runTest {
        val repo = object : FakeUsuarioCacheRepository() {
            override suspend fun limpiarCache() {
                throw Exception("Error de prueba")
            }
        }
        try {
            LimpiarPerfilCacheadoUseCase(repo)()
            assertTrue(false)
        } catch (e: Exception) {
            assertTrue(e.message == "Error de prueba")
        }
    }
}
