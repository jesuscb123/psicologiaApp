package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeNotificacionesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DarDeBajaFcmTokenUseCaseTest {

    @Test
    fun `invoke debe obtener token y dar de baja en el repositorio`() = runTest {
        val repo = object : FakeNotificacionesRepository() {
            override suspend fun obtenerTokenFcmActual(): Result<String> = Result.success("token-abc")
            override suspend fun darDeBajaToken(token: String): Result<Unit> = Result.success(Unit)
        }
        val resultado = DarDeBajaFcmTokenUseCase(repo)()
        assertTrue(resultado.isSuccess)
    }

    @Test
    fun `invoke debe propagar fallo al obtener token`() = runTest {
        val repo = object : FakeNotificacionesRepository() {
            override suspend fun obtenerTokenFcmActual(): Result<String> =
                Result.failure<String>(Exception("Error de prueba"))
        }
        val resultado = DarDeBajaFcmTokenUseCase(repo)()
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
