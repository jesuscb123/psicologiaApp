package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarPacientesDePsicologoUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = listOf(Paciente(1L, "uid", "Ana", "Lopez", null, 2L, 10L))
        val repo = object : FakePsicologoRepository() {
            override fun observarPacientesDePsicologo() = flowOf(expected)
        }
        val actual = ObservarPacientesDePsicologoUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
