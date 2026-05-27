package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeAlertaRiesgoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarcarAlertaRiesgoUseCaseTest {

    @Test
    fun `invoke con parametros validos delega en el repositorio`() = runTest {
        var invocado = false
        val repo = object : FakeAlertaRiesgoRepository() {
            override suspend fun marcarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {
                invocado = true
            }
        }
        MarcarAlertaRiesgoUseCase(repo)(psicologoUid = "uid", pacienteId = 1L)
        assertTrue(invocado)
    }

    @Test
    fun `invoke con parametros invalidos no delega en el repositorio`() = runTest {
        var invocado = false
        val repo = object : FakeAlertaRiesgoRepository() {
            override suspend fun marcarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {
                invocado = true
            }
        }
        MarcarAlertaRiesgoUseCase(repo)(psicologoUid = "", pacienteId = 0L)
        assertFalse(invocado)
    }
}
