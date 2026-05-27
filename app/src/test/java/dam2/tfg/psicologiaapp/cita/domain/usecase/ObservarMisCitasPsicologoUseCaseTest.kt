package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaPersistido
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarMisCitasPsicologoUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = listOf(Cita(1L, "2024-01-01T10:00:00+01:00", "2024-01-01T11:00:00+01:00", 1L, 2L, "Dr", "Pac", EstadoCitaPersistido.RESERVADA, EstadoCitaCalculado.ACTIVA))
        val repo = object : FakeCitaRepository() {
            override fun observarMisCitasPsicologo() = flowOf(expected)
        }
        val actual = ObservarMisCitasPsicologoUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
