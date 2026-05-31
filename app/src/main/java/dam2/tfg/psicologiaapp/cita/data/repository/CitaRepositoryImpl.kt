package dam2.tfg.psicologiaapp.cita.data.repository

import dam2.tfg.psicologiaapp.cita.data.local.CitaDao
import dam2.tfg.psicologiaapp.cita.data.mappers.toDomain
import dam2.tfg.psicologiaapp.cita.data.mappers.toEntity
import dam2.tfg.psicologiaapp.cita.data.remote.CitaApi
import dam2.tfg.psicologiaapp.cita.data.remote.CitaCrearRequestDto
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import dam2.tfg.psicologiaapp.util.runSuspendCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CitaRepositoryImpl @Inject constructor(
    private val citaApi: CitaApi,
    private val citaDao: CitaDao,
) : CitaRepository {

    override suspend fun getDisponibilidadDia(
        fecha: LocalDate,
        zonaHoraria: String,
    ): Result<DisponibilidadDia> = runSuspendCatching {
        val respuesta = citaApi.getDisponibilidadDia(
            fechaIso = fecha.toString(),
            zonaHoraria = zonaHoraria,
        )

        if (respuesta.code() == 204) {
            return@runSuspendCatching DisponibilidadDia(
                fecha = fecha,
                zonaHoraria = zonaHoraria,
                horasDisponibles = emptyList(),
            )
        }
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al obtener disponibilidad: HTTP ${respuesta.code()}")
        }
        val cuerpo = respuesta.body() ?: throw IllegalStateException("Respuesta vacía del servidor")
        cuerpo.toDomain()
    }

    override suspend fun reservarCita(
        inicioIsoOffset: String,
        zonaHoraria: String,
    ): Result<Cita> = runSuspendCatching {
        val respuesta = citaApi.reservarCita(
            CitaCrearRequestDto(inicio = inicioIsoOffset, zonaHoraria = zonaHoraria)
        )
        if (!respuesta.isSuccessful) {
            val detalle = respuesta.errorBody()?.use { it.string() }?.trim()?.takeIf { it.isNotEmpty() }
            val mensaje = when (respuesta.code()) {
                409 -> detalle ?: "Ese horario ya está reservado"
                else -> detalle ?: "Error al reservar cita: HTTP ${respuesta.code()}"
            }
            throw IllegalStateException(mensaje)
        }
        val cuerpo = respuesta.body() ?: throw IllegalStateException("Respuesta vacía del servidor")
        val cita = cuerpo.toDomain()
        // Persist immediately — both roles may have a record of this cita.
        citaDao.guardarTodas(listOf(cuerpo.toEntity(esDePaciente = true)))
        cita
    }

    override suspend fun getMisCitasPaciente(): Result<List<Cita>> = runSuspendCatching {
        val respuesta = citaApi.getMisCitasPaciente()
        if (respuesta.code() == 204) {
            citaDao.borrarCitasPaciente()
            return@runSuspendCatching emptyList()
        }
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al obtener citas: HTTP ${respuesta.code()}")
        }
        val lista = respuesta.body().orEmpty()
        citaDao.borrarCitasPaciente()
        citaDao.guardarTodas(lista.map { it.toEntity(esDePaciente = true) })
        lista.map { it.toDomain() }
    }

    override suspend fun getMisCitasPsicologo(): Result<List<Cita>> = runSuspendCatching {
        val respuesta = citaApi.getMisCitasPsicologo()
        if (respuesta.code() == 204) {
            citaDao.borrarCitasPsicologo()
            return@runSuspendCatching emptyList()
        }
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al obtener citas: HTTP ${respuesta.code()}")
        }
        val lista = respuesta.body().orEmpty()
        citaDao.borrarCitasPsicologo()
        citaDao.guardarTodas(lista.map { it.toEntity(esDePaciente = false) })
        lista.map { it.toDomain() }
    }

    override suspend fun cancelarCita(citaId: Long): Result<Cita> = runSuspendCatching {
        val respuesta = citaApi.cancelarCita(citaId)
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al cancelar cita: HTTP ${respuesta.code()}")
        }
        val cuerpo = respuesta.body() ?: throw IllegalStateException("Respuesta vacía del servidor")
        val cita = cuerpo.toDomain()
        // Update Room with the cancelled state so the Flow emits immediately.
        citaDao.guardarTodas(listOf(cuerpo.toEntity(esDePaciente = true)))
        citaDao.guardarTodas(listOf(cuerpo.toEntity(esDePaciente = false)))
        cita
    }

    override fun observarMisCitasPaciente(): Flow<List<Cita>> =
        citaDao.observarCitasPaciente().map { lista -> lista.map { it.toDomain() } }

    override fun observarMisCitasPsicologo(): Flow<List<Cita>> =
        citaDao.observarCitasPsicologo().map { lista -> lista.map { it.toDomain() } }

    override suspend fun sincronizarMisCitasPaciente(): Result<Unit> = runSuspendCatching {
        getMisCitasPaciente().getOrThrow()
        Unit
    }

    override suspend fun sincronizarMisCitasPsicologo(): Result<Unit> = runSuspendCatching {
        getMisCitasPsicologo().getOrThrow()
        Unit
    }
}

