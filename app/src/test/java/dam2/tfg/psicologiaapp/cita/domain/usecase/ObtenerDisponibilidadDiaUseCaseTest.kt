package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import java.time.LocalDate
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObtenerDisponibilidadDiaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = DisponibilidadDia(LocalDate.of(2024, 1, 1), "Europe/Madrid")
        val repo = object : FakeCitaRepository() {
            override suspend fun getDisponibilidadDia(fecha: java.time.LocalDate, zonaHoraria: String) = Result.success(expected)
        }
        val useCase = ObtenerDisponibilidadDiaUseCase(repo)
        val resultado = useCase(fecha = LocalDate.of(2024, 1, 1), zonaHoraria = "Europe/Madrid")

        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeCitaRepository() {
            override suspend fun getDisponibilidadDia(fecha: java.time.LocalDate, zonaHoraria: String) =
                Result.failure<DisponibilidadDia>(Exception("Error de prueba"))
        }
        val useCase = ObtenerDisponibilidadDiaUseCase(repo)
        val resultado = useCase(fecha = LocalDate.of(2024, 1, 1), zonaHoraria = "Europe/Madrid")

        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
