package dam2.tfg.psicologiaapp.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

class DictadoVozAppTest {

    @Test
    fun combinarTextoDictado_campoVacio_devuelveTextoReconocido() {
        assertEquals("hola", combinarTextoDictado("", "hola"))
    }

    @Test
    fun combinarTextoDictado_campoConTexto_concatenaConEspacio() {
        assertEquals("hola mundo", combinarTextoDictado("hola", "mundo"))
    }

    @Test
    fun combinarTextoDictado_reconocidoVacio_devuelveActual() {
        assertEquals("hola", combinarTextoDictado("hola", ""))
        assertEquals("hola", combinarTextoDictado("hola", "   "))
    }

    @Test
    fun combinarTextoDictado_reconocidoConEspacios_recorta() {
        assertEquals("hola mundo", combinarTextoDictado("hola", "  mundo  "))
    }
}
