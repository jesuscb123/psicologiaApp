package dam2.tfg.psicologiaapp.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class FormateoFechaAppTest {

    private val formatoEsperado = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.getDefault())

    @Test
    fun `formatearFechaLista parsea OffsetDateTime ISO`() {
        val iso = "2026-05-27T10:30:00+02:00"
        val esperado = LocalDate.of(2026, 5, 27).format(formatoEsperado)

        assertEquals(esperado, formatearFechaLista(iso))
    }

    @Test
    fun `formatearFechaLista parsea LocalDateTime ISO`() {
        val iso = "2026-05-27T10:30:00"
        val esperado = LocalDate.of(2026, 5, 27).format(formatoEsperado)

        assertEquals(esperado, formatearFechaLista(iso))
    }

    @Test
    fun `formatearFechaLista parsea Instant ISO`() {
        val iso = "2026-05-27T08:30:00Z"
        val resultado = formatearFechaLista(iso)

        assertTrue(resultado.contains("2026"))
        assertTrue(resultado.matches(Regex("\\d{2} .+, \\d{4}")))
    }

    @Test
    fun `formatearFechaLista devuelve original si no puede parsear`() {
        val invalido = "fecha-no-valida"
        assertEquals(invalido, formatearFechaLista(invalido))
    }

    @Test
    fun `parsearFechaNotaLocal parsea OffsetDateTime ISO`() {
        assertEquals(LocalDate.of(2026, 5, 27), parsearFechaNotaLocal("2026-05-27T10:30:00+02:00"))
    }

    @Test
    fun `parsearFechaNotaLocal parsea LocalDateTime ISO`() {
        assertEquals(LocalDate.of(2026, 5, 27), parsearFechaNotaLocal("2026-05-27T10:30:00"))
    }

    @Test
    fun `parsearFechaNotaLocal devuelve null si no puede parsear`() {
        assertEquals(null, parsearFechaNotaLocal("fecha-no-valida"))
    }
}
