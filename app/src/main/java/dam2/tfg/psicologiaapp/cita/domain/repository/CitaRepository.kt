package dam2.tfg.psicologiaapp.cita.domain.repository

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import java.time.LocalDate

interface CitaRepository {

    suspend fun getDisponibilidadDia(fecha: LocalDate, zonaHoraria: String): Result<DisponibilidadDia>

    suspend fun reservarCita(inicioIsoOffset: String, zonaHoraria: String): Result<Cita>

    suspend fun getMisCitasPaciente(): Result<List<Cita>>

    suspend fun getMisCitasPsicologo(): Result<List<Cita>>

    suspend fun cancelarCita(citaId: Long): Result<Cita>
}

