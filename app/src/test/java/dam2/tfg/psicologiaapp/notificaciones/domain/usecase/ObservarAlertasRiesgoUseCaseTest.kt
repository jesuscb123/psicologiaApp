package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeAlertaRiesgoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarAlertasRiesgoUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = setOf(1L, 2L)
        val repo = object : FakeAlertaRiesgoRepository() {
            override fun observarAlertasRiesgo(psicologoUid: String) = flowOf(expected)
        }
        val actual = ObservarAlertasRiesgoUseCase(repo)(psicologoUid = "uid").first()
        assertEquals(expected, actual)
    }
}
