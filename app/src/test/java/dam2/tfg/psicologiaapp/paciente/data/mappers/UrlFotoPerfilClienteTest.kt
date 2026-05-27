package dam2.tfg.psicologiaapp.paciente.data.mappers

import dam2.tfg.psicologiaapp.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrlFotoPerfilClienteTest {

    @Test
    fun `devuelve null si url es null`() {
        assertNull(normalizarUrlFotoPerfilCliente(null))
    }

    @Test
    fun `devuelve null si url esta vacia o en blanco`() {
        assertNull(normalizarUrlFotoPerfilCliente(""))
        assertNull(normalizarUrlFotoPerfilCliente("   "))
    }

    @Test
    fun `conserva url remota sin localhost`() {
        val url = "https://cdn.example.com/fotos/user.jpg"
        assertEquals(url, normalizarUrlFotoPerfilCliente(url))
    }

    @Test
    fun `reemplaza localhost por BASE_URL manteniendo ruta api`() {
        val url = "http://localhost:8080/api/usuarios/1/foto.jpg"
        val base = BuildConfig.BASE_URL.trimEnd('/')
        assertEquals("$base/api/usuarios/1/foto.jpg", normalizarUrlFotoPerfilCliente(url))
    }

    @Test
    fun `reemplaza 127_0_0_1 por BASE_URL`() {
        val url = "http://127.0.0.1:8080/api/foto.jpg"
        val base = BuildConfig.BASE_URL.trimEnd('/')
        assertEquals("$base/api/foto.jpg", normalizarUrlFotoPerfilCliente(url))
    }

    @Test
    fun `reemplaza 10_0_2_2 por BASE_URL`() {
        val url = "http://10.0.2.2:8080/api/foto.jpg"
        val base = BuildConfig.BASE_URL.trimEnd('/')
        assertEquals("$base/api/foto.jpg", normalizarUrlFotoPerfilCliente(url))
    }

    @Test
    fun `conserva url local si no contiene segmento api`() {
        val url = "http://localhost:8080/static/foto.jpg"
        assertEquals(url, normalizarUrlFotoPerfilCliente(url))
    }

    @Test
    fun `recorta espacios antes de procesar`() {
        val url = "  http://localhost:8080/api/foto.jpg  "
        val base = BuildConfig.BASE_URL.trimEnd('/')
        assertEquals("$base/api/foto.jpg", normalizarUrlFotoPerfilCliente(url))
    }
}
