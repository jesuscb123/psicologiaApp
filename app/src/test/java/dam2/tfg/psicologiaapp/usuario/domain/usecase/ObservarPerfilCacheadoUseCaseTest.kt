package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarPerfilCacheadoUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = PerfilCacheado(1L, "uid", "Nombre", "Apellidos", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioCacheRepository() {
            override fun observarPerfilCacheado() = flowOf(expected)
        }
        val actual = ObservarPerfilCacheadoUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
