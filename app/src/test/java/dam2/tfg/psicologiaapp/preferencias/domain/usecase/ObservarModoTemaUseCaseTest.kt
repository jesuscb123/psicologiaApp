package dam2.tfg.psicologiaapp.preferencias.domain.usecase

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.test.fakes.FakeTemaPreferenciasRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarModoTemaUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = ModoTemaApp.Oscuro
        val repo = object : FakeTemaPreferenciasRepository() {
            override fun observarModoTema() = flowOf(expected)
        }
        val actual = ObservarModoTemaUseCase(repo)().first()
        assertEquals(expected, actual)
    }
}
