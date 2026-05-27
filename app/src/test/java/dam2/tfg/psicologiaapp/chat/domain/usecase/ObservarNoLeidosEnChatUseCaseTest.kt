package dam2.tfg.psicologiaapp.chat.domain.usecase

import dam2.tfg.psicologiaapp.test.fakes.FakeChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarNoLeidosEnChatUseCaseTest {

    @Test
    fun `invoke debe emitir valores del repositorio`() = runTest {
        val expected = setOf("chat-1")
        val repo = object : FakeChatRepository() {
            override fun observarNoLeidosChatIds(miUid: String) = flowOf(expected)
        }
        val actual = ObservarNoLeidosEnChatUseCase(repo)(miUid = "uid").first()
        assertEquals(expected, actual)
    }
}
