package dam2.tfg.psicologiaapp.presentation.ui.paciente.citas

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaPersistido
import dam2.tfg.psicologiaapp.cita.domain.usecase.ObtenerDisponibilidadDiaUseCase
import dam2.tfg.psicologiaapp.cita.domain.usecase.ReservarCitaUseCase
import dam2.tfg.psicologiaapp.test.MainDispatcherRule
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class CitasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `seleccionarFecha actualiza la fecha y carga disponibilidad`() = runTest {
        val fecha = LocalDate.of(2026, 6, 1)
        val disponibilidad = DisponibilidadDia(fecha, "UTC", listOf(LocalTime.of(10, 0)))
        val viewModel = crearViewModel(
            resultadoDispo = { _, _ -> Result.success(disponibilidad) },
        )

        viewModel.seleccionarFecha(fecha)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(fecha, estado.fechaSeleccionada)
        assertEquals(disponibilidad, estado.disponibilidad)
        assertNull(estado.mensajeError)
    }

    @Test
    fun `seleccionarFecha en fin de semana muestra mensaje de error`() = runTest {
        val sabado = LocalDate.of(2026, 5, 30)
        val viewModel = crearViewModel()

        viewModel.seleccionarFecha(sabado)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals("No hay citas disponibles los fines de semana.", estado.mensajeError)
        assertNull(estado.disponibilidad)
    }

    @Test
    fun `seleccionarHora actualiza el estado`() {
        val viewModel = crearViewModel()
        val hora = LocalTime.of(15, 30)

        viewModel.seleccionarHora(hora)

        assertEquals(hora, viewModel.uiState.value.horaSeleccionada)
    }

    @Test
    fun `reservar sin hora seleccionada muestra error`() {
        val viewModel = crearViewModel()

        viewModel.reservar()

        assertEquals("Selecciona una hora", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun `reservar con exito navega y limpia seleccion`() = runTest {
        val fecha = LocalDate.of(2026, 6, 1)
        val hora = LocalTime.of(10, 0)
        val cita = Cita(1, "", "", 1, 1, "Psico", "Pac", EstadoCitaPersistido.RESERVADA, EstadoCitaCalculado.ACTIVA)

        val viewModel = crearViewModel(
            resultadoReservar = { _, _ -> Result.success(cita) },
        )

        viewModel.seleccionarFecha(fecha)
        viewModel.seleccionarHora(hora)
        viewModel.reservar()

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertNull(estado.horaSeleccionada)
        assertEquals(EventoNavegacionCitas.CitaReservada, estado.eventoNavegacion)
    }

    @Test
    fun `reservar con fallo muestra error`() = runTest {
        val fecha = LocalDate.of(2026, 6, 1)
        val hora = LocalTime.of(10, 0)
        val errorMsg = "Error en reserva"

        val viewModel = crearViewModel(
            resultadoReservar = { _, _ -> Result.failure(Exception(errorMsg)) },
            resultadoDispo = { _, _ -> Result.failure(Exception(errorMsg)) },
        )

        viewModel.seleccionarFecha(fecha)
        viewModel.seleccionarHora(hora)
        viewModel.reservar()

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(errorMsg, estado.mensajeError)
        assertNull(estado.horaSeleccionada)
    }

    @Test
    fun alConsumirEventoNavegacion_limpiaEvento() = runTest {
        val viewModel = crearViewModel(
            resultadoReservar = { _, _ ->
                Result.success(
                    Cita(1, "", "", 1, 1, "P", "A", EstadoCitaPersistido.RESERVADA, EstadoCitaCalculado.ACTIVA),
                )
            },
        )
        val fecha = LocalDate.of(2026, 6, 2)
        viewModel.seleccionarFecha(fecha)
        viewModel.seleccionarHora(LocalTime.of(9, 0))
        viewModel.reservar()
        advanceUntilIdle()

        viewModel.alConsumirEventoNavegacion()

        assertNull(viewModel.uiState.value.eventoNavegacion)
    }

    @Test
    fun cargarDisponibilidad_fallo_muestraError() = runTest {
        val viewModel = crearViewModel(
            resultadoDispo = { _, _ -> Result.failure(Exception("Servidor caído")) },
        )
        viewModel.seleccionarFecha(LocalDate.of(2026, 6, 3))
        advanceUntilIdle()

        assertEquals("Servidor caído", viewModel.uiState.value.mensajeError)
    }

    private fun crearViewModel(
        resultadoDispo: suspend (LocalDate, String) -> Result<DisponibilidadDia> = { f, z ->
            Result.success(DisponibilidadDia(f, z))
        },
        resultadoReservar: suspend (String, String) -> Result<Cita> = { _, _ ->
            Result.failure(Exception("Not implemented"))
        },
    ): CitasViewModel {
        val repo = object : FakeCitaRepository() {
            override suspend fun getDisponibilidadDia(fecha: LocalDate, zonaHoraria: String) =
                resultadoDispo(fecha, zonaHoraria)
            override suspend fun reservarCita(inicioIsoOffset: String, zonaHoraria: String) =
                resultadoReservar(inicioIsoOffset, zonaHoraria)
        }
        return CitasViewModel(
            ObtenerDisponibilidadDiaUseCase(repo),
            ReservarCitaUseCase(repo),
        )
    }
}
