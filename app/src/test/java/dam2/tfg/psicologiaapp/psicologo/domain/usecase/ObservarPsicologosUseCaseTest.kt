package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarPsicologosUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = listOf(Psicologo(1L, 10L, "uid", "Dr", "House", null, "12345", listOf("Clinica"), "Desc"))
        val repo = object : FakePsicologoRepository() {
            override fun observarPsicologos() = flowOf(expected)
        }
        val actual = ObservarPsicologosUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
