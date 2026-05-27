package dam2.tfg.psicologiaapp.notificaciones.data.repository

import dam2.tfg.psicologiaapp.notificaciones.data.remote.EliminarFcmTokenRequestDto
import dam2.tfg.psicologiaapp.notificaciones.data.remote.FcmFuenteDatos
import dam2.tfg.psicologiaapp.notificaciones.data.remote.NotificacionesApi
import dam2.tfg.psicologiaapp.notificaciones.data.remote.NotificarMensajeChatRequestDto
import dam2.tfg.psicologiaapp.notificaciones.data.remote.RegistrarFcmTokenRequestDto
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

class NotificacionesRepositoryImplTest {

    private val api = FakeNotificacionesApi()
    private val fcmFuenteDatos = mock<FcmFuenteDatos>()
    private lateinit var repository: NotificacionesRepositoryImpl

    @Before
    fun setUp() {
        repository = NotificacionesRepositoryImpl(api, fcmFuenteDatos)
    }

    @Test
    fun `registrarTokenActual debe enviar token e instalacion a la API`() = runTest {
        whenever(fcmFuenteDatos.obtenerIdInstalacion()).thenReturn("inst-1")
        api.respuestaRegistrar = Response.success(Unit)

        val resultado = repository.registrarTokenActual("token-fcm")

        assertTrue(resultado.isSuccess)
        assertEquals(
            RegistrarFcmTokenRequestDto(
                token = "token-fcm",
                instalacionId = "inst-1",
                plataforma = "ANDROID",
            ),
            api.ultimoRegistrar,
        )
    }

    @Test
    fun `registrarTokenActual debe propagar error HTTP`() = runTest {
        whenever(fcmFuenteDatos.obtenerIdInstalacion()).thenReturn(null)
        api.respuestaRegistrar = Response.error(500, "".toResponseBody(null))

        val resultado = repository.registrarTokenActual("token-fcm")

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("HTTP 500") == true)
    }

    @Test
    fun `darDeBajaToken debe llamar a la API`() = runTest {
        api.respuestaEliminar = Response.success(Unit)

        val resultado = repository.darDeBajaToken("token-fcm")

        assertTrue(resultado.isSuccess)
        assertEquals(EliminarFcmTokenRequestDto("token-fcm"), api.ultimoEliminar)
    }

    @Test
    fun `obtenerTokenFcmActual debe devolver token no vacio`() = runTest {
        whenever(fcmFuenteDatos.obtenerTokenActual()).thenReturn("  token-abc  ")

        val resultado = repository.obtenerTokenFcmActual()

        assertTrue(resultado.isSuccess)
        assertEquals("token-abc", resultado.getOrNull())
    }

    @Test
    fun `obtenerTokenFcmActual debe fallar si el token esta vacio`() = runTest {
        whenever(fcmFuenteDatos.obtenerTokenActual()).thenReturn("   ")

        val resultado = repository.obtenerTokenFcmActual()

        assertTrue(resultado.isFailure)
        assertEquals("FCM devolvió un token vacío", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `notificarMensajeChat debe enviar chatId y vista previa`() = runTest {
        api.respuestaNotificar = Response.success(Unit)

        val resultado = repository.notificarMensajeChat("chat-1", "Hola")

        assertTrue(resultado.isSuccess)
        assertEquals(
            NotificarMensajeChatRequestDto(chatId = "chat-1", vistaPreviaTexto = "Hola"),
            api.ultimoNotificar,
        )
    }

    @Test
    fun `notificarMensajeChat debe propagar error HTTP`() = runTest {
        api.respuestaNotificar = Response.error(503, "".toResponseBody(null))

        val resultado = repository.notificarMensajeChat("chat-1", "Hola")

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("HTTP 503") == true)
    }

    private class FakeNotificacionesApi : NotificacionesApi {
        var respuestaRegistrar: Response<Unit> = Response.success(Unit)
        var respuestaEliminar: Response<Unit> = Response.success(Unit)
        var respuestaNotificar: Response<Unit> = Response.success(Unit)
        var ultimoRegistrar: RegistrarFcmTokenRequestDto? = null
        var ultimoEliminar: EliminarFcmTokenRequestDto? = null
        var ultimoNotificar: NotificarMensajeChatRequestDto? = null

        override suspend fun registrarToken(request: RegistrarFcmTokenRequestDto): Response<Unit> {
            ultimoRegistrar = request
            return respuestaRegistrar
        }

        override suspend fun eliminarToken(request: EliminarFcmTokenRequestDto): Response<Unit> {
            ultimoEliminar = request
            return respuestaEliminar
        }

        override suspend fun notificarMensajeChat(request: NotificarMensajeChatRequestDto): Response<Unit> {
            ultimoNotificar = request
            return respuestaNotificar
        }
    }
}
