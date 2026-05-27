package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarTareasPacienteActualUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = listOf(Tarea(1L, "Titulo", "Desc", "2024-01-01", false, true, 2L, 1L))
        val repo = object : FakeTareaRepository() {
            override fun observarTareasPacienteActual() = flowOf(expected)
        }
        val actual = ObservarTareasPacienteActualUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
