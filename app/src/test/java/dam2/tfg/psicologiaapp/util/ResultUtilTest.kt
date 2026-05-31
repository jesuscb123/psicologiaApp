package dam2.tfg.psicologiaapp.util

import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResultUtilTest {

    @Test
    fun mensajeErrorParaUi_devuelveNull_cuandoEsCancelacion() {
        assertNull(CancellationException("StandaloneCoroutine was cancelled").mensajeErrorParaUi())
    }

    @Test
    fun mensajeErrorParaUi_devuelveMensaje_cuandoEsErrorReal() {
        assertEquals(
            "Error de red",
            Exception("Error de red").mensajeErrorParaUi(),
        )
    }
}
