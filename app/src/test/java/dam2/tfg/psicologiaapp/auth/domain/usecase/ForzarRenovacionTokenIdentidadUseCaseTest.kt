package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ForzarRenovacionTokenIdentidadUseCaseTest {

    @Test
    fun `invoke debe delegar en el repositorio`() = runTest {
        var invocado = false
        val repo = object : FakeAuthRepository() {
            override suspend fun forzarRenovacionTokenIdentidad() {
                invocado = true
            }
        }
        ForzarRenovacionTokenIdentidadUseCase(repo)()
        assertTrue(invocado)
    }

    @Test
    fun `invoke debe propagar excepcion del repositorio`() = runTest {
        val repo = object : FakeAuthRepository() {
            override suspend fun forzarRenovacionTokenIdentidad() {
                throw Exception("Error de prueba")
            }
        }
        try {
            ForzarRenovacionTokenIdentidadUseCase(repo)()
            assertTrue(false)
        } catch (e: Exception) {
            assertTrue(e.message == "Error de prueba")
        }
    }
}
