package dam2.tfg.psicologiaapp.psicologo.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class StringListConverterTest {

    private val converter = StringListConverter()

    @Test
    fun `fromString devuelve lista vacia si value es null`() {
        assertEquals(emptyList<String>(), converter.fromString(null))
    }

    @Test
    fun `fromString devuelve lista vacia si value esta en blanco`() {
        assertEquals(emptyList<String>(), converter.fromString(""))
        assertEquals(emptyList<String>(), converter.fromString("   "))
    }

    @Test
    fun `fromString parsea JSONArray valido`() {
        assertEquals(
            listOf("Clinica", "Infantil"),
            converter.fromString("""["Clinica","Infantil"]"""),
        )
    }

    @Test
    fun `fromString devuelve lista vacia si JSON es invalido`() {
        assertEquals(emptyList<String>(), converter.fromString("no-json"))
    }

    @Test
    fun `toString devuelve array vacio para lista null o vacia`() {
        assertEquals("[]", converter.toString(null))
        assertEquals("[]", converter.toString(emptyList()))
    }

    @Test
    fun `round-trip toString y fromString conserva lista`() {
        val original = listOf("Clinica", "Deportiva", "Infantil")
        assertEquals(original, converter.fromString(converter.toString(original)))
    }
}
