package dam2.tfg.psicologiaapp.cita.data.repository

import dam2.tfg.psicologiaapp.cita.data.mappers.toDomain
import dam2.tfg.psicologiaapp.cita.data.remote.CitaApi
import dam2.tfg.psicologiaapp.cita.data.remote.CitaCrearRequestDto
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitaRepositoryImpl @Inject constructor(
    private val citaApi: CitaApi,
) : CitaRepository {

    override suspend fun getDisponibilidadDia(
        fecha: LocalDate,
        zonaHoraria: String,
    ): Result<DisponibilidadDia> = runCatching {
        val respuesta = citaApi.getDisponibilidadDia(
            fechaIso = fecha.toString(),
            zonaHoraria = zonaHoraria,
        )

        if (respuesta.code() == 204) {
            return@runCatching DisponibilidadDia(
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
    ): Result<Cita> = runCatching {
        val respuesta = citaApi.reservarCita(
            CitaCrearRequestDto(inicioIsoOffset = inicioIsoOffset, zonaHoraria = zonaHoraria)
        )
        if (respuesta.code() == 409) {
            throw IllegalStateException("Ese horario ya está reservado")
        }
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al reservar cita: HTTP ${respuesta.code()}")
        }
        val cuerpo = respuesta.body() ?: throw IllegalStateException("Respuesta vacía del servidor")
        cuerpo.toDomain()
    }

    override suspend fun getMisCitasPaciente(): Result<List<Cita>> = runCatching {
        val respuesta = citaApi.getMisCitasPaciente()
        if (respuesta.code() == 204) return@runCatching emptyList()
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al obtener citas: HTTP ${respuesta.code()}")
        }
        respuesta.body().orEmpty().map { it.toDomain() }
    }

    override suspend fun getMisCitasPsicologo(): Result<List<Cita>> = runCatching {
        val respuesta = citaApi.getMisCitasPsicologo()
        if (respuesta.code() == 204) return@runCatching emptyList()
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al obtener citas: HTTP ${respuesta.code()}")
        }
        respuesta.body().orEmpty().map { it.toDomain() }
    }

    override suspend fun cancelarCita(citaId: Long): Result<Cita> = runCatching {
        val respuesta = citaApi.cancelarCita(citaId)
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al cancelar cita: HTTP ${respuesta.code()}")
        }
        val cuerpo = respuesta.body() ?: throw IllegalStateException("Respuesta vacía del servidor")
        cuerpo.toDomain()
    }
}

