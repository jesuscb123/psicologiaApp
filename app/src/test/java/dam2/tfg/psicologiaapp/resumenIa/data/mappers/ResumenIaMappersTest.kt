package dam2.tfg.psicologiaapp.resumenIa.data.mappers

import dam2.tfg.psicologiaapp.resumenIa.data.remote.ResumenIaResponseDto
import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa
import org.junit.Assert.assertEquals
import org.junit.Test

class ResumenIaMappersTest {

    private val dto = ResumenIaResponseDto(
        resumen = "El paciente muestra mejoría progresiva.",
        numeroNotasAnalizadas = 5,
        generadoEn = "2026-05-27T12:00:00",
        modelo = "gpt-4",
    )

    private val domain = ResumenIa(
        resumen = "El paciente muestra mejoría progresiva.",
        numeroNotasAnalizadas = 5,
        generadoEn = "2026-05-27T12:00:00",
        modelo = "gpt-4",
    )

    @Test
    fun `ResumenIaResponseDto toDomain mapea todos los campos`() {
        assertEquals(domain, dto.toDomain())
    }
}
