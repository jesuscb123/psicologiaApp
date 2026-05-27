package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakeCitaRepository : CitaRepository {
    override suspend fun getDisponibilidadDia(fecha: LocalDate, zonaHoraria: String): Result<DisponibilidadDia> =
        Result.failure(NotImplementedError())
    override suspend fun reservarCita(inicioIsoOffset: String, zonaHoraria: String): Result<Cita> =
        Result.failure(NotImplementedError())
    override suspend fun getMisCitasPaciente(): Result<List<Cita>> = Result.success(emptyList())
    override suspend fun getMisCitasPsicologo(): Result<List<Cita>> = Result.success(emptyList())
    override suspend fun cancelarCita(citaId: Long): Result<Cita> = Result.failure(NotImplementedError())
    override fun observarMisCitasPaciente(): Flow<List<Cita>> = emptyFlow()
    override fun observarMisCitasPsicologo(): Flow<List<Cita>> = emptyFlow()
    override suspend fun sincronizarMisCitasPaciente(): Result<Unit> = Result.success(Unit)
    override suspend fun sincronizarMisCitasPsicologo(): Result<Unit> = Result.success(Unit)
}
