package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ObtenerPerfilCacheadoUseCaseTest {

    @Test
    fun `invoke debe devolver perfil cacheado del repositorio`() = runTest {
        val expected = PerfilCacheado(1L, "uid", "Nombre", "Apellidos", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioCacheRepository() {
            override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado? = expected
        }
        val resultado = ObtenerPerfilCacheadoUseCase(repo)("uid")
        assertEquals(expected, resultado)
    }

    @Test
    fun `invoke debe devolver null si no hay perfil cacheado`() = runTest {
        val repo = FakeUsuarioCacheRepository()
        val resultado = ObtenerPerfilCacheadoUseCase(repo)("uid")
        assertNull(resultado)
    }
}
