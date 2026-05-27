package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarMensajesChatUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = listOf(MensajeChat("m1", "hola", "uid", 1L))
        val repo = object : FakeChatRepository() {
            override fun observarMensajes(rtdbRuta: String) = flowOf(expected)
        }
        val actual = ObservarMensajesChatUseCase(repo)(rtdbRuta = "ruta").first()
        assertEquals(expected, actual)
    }
}
