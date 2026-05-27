package dam2.tfg.psicologiaapp.resumenIa.data.repository

import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.resumenIa.data.remote.ResumenIaApi
import dam2.tfg.psicologiaapp.resumenIa.data.remote.ResumenIaResponseDto
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

class ResumenIaRepositoryImplTest {

    private val api = FakeResumenIaApi()
    private val proveedorToken = mock<ProveedorTokenFirebase>()
    private lateinit var repository: ResumenIaRepositoryImpl

    @Before
    fun setUp() {
        repository = ResumenIaRepositoryImpl(api, proveedorToken)
    }

    @Test
    fun `generarResumenNotasPaciente debe mapear respuesta exitosa`() = runTest {
        api.respuesta = Response.success(
            ResumenIaResponseDto(
                resumen = "Resumen generado",
                numeroNotasAnalizadas = 3,
                generadoEn = "2026-05-27T10:00:00",
                modelo = "llama",
            ),
        )

        val resultado = repository.generarResumenNotasPaciente(42L)

        assertTrue(resultado.isSuccess)
        assertEquals("Resumen generado", resultado.getOrNull()?.resumen)
        assertEquals(3, resultado.getOrNull()?.numeroNotasAnalizadas)
        assertEquals(42L, api.ultimoPacienteId)
    }

    @Test
    fun `generarResumenNotasPaciente debe reintentar tras 401`() = runTest {
        api.respuestaSecuencia = listOf(
            Response.error(401, "".toResponseBody(null)),
            Response.success(
                ResumenIaResponseDto(
                    resumen = "Ok",
                    numeroNotasAnalizadas = 1,
                    generadoEn = "2026-05-27T10:00:00",
                    modelo = "llama",
                ),
            ),
        )
        whenever(proveedorToken.obtenerToken(forzarRenovacion = true)).thenReturn("token-nuevo")

        val resultado = repository.generarResumenNotasPaciente(1L)

        assertTrue(resultado.isSuccess)
        assertEquals("Ok", resultado.getOrNull()?.resumen)
        verify(proveedorToken).obtenerToken(forzarRenovacion = true)
        assertEquals(2, api.llamadas)
    }

    @Test
    fun `generarResumenNotasPaciente debe propagar error 404`() = runTest {
        api.respuesta = Response.error(404, "".toResponseBody(null))

        val resultado = repository.generarResumenNotasPaciente(1L)

        assertTrue(resultado.isFailure)
        assertTrue(
            resultado.exceptionOrNull()?.message?.contains("no tiene notas") == true,
        )
    }

    @Test
    fun `generarResumenNotasPaciente debe propagar error 503`() = runTest {
        api.respuesta = Response.error(503, "".toResponseBody(null))

        val resultado = repository.generarResumenNotasPaciente(1L)

        assertTrue(resultado.isFailure)
        assertTrue(
            resultado.exceptionOrNull()?.message?.contains("no está disponible") == true,
        )
    }

    private class FakeResumenIaApi : ResumenIaApi {
        var respuesta: Response<ResumenIaResponseDto> = Response.success(
            ResumenIaResponseDto("", 0, "", ""),
        )
        var respuestaSecuencia: List<Response<ResumenIaResponseDto>>? = null
        var ultimoPacienteId: Long? = null
        var llamadas = 0

        override suspend fun generarResumenNotasPaciente(pacienteId: Long): Response<ResumenIaResponseDto> {
            ultimoPacienteId = pacienteId
            llamadas++
            return respuestaSecuencia?.getOrNull(llamadas - 1) ?: respuesta
        }
    }
}
