package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarNotasPacienteActualUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = listOf(Nota(1L, "Asunto", "Desc", "2024-01-01", 1L, 2L))
        val repo = object : FakeNotaRepository() {
            override fun observarNotasPacienteActual() = flowOf(expected)
        }
        val actual = ObservarNotasPacienteActualUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
