package dam2.tfg.psicologiaapp.tarea.data.repository

import dam2.tfg.psicologiaapp.tarea.data.mappers.toDomain
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaActualizarRealizadaRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaActualizarRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaApi
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaCrearRequestDto
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TareaRepositoryImpl @Inject constructor(
    private val tareaApi: TareaApi
) : TareaRepository {

    override suspend fun getTareasPacienteActual(): Result<List<Tarea>> = runCatching {
        tareaApi.getTareasPacienteActual().map { it.toDomain() }
    }

    override suspend fun getTareasDePaciente(pacienteId: Long): Result<List<Tarea>> = runCatching {
        tareaApi.getTareasDePaciente(pacienteId).map { it.toDomain() }
    }

    override suspend fun crearTarea(
        pacienteId: Long,
        titulo: String,
        descripcion: String
    ): Result<Tarea> = runCatching {
        tareaApi.crearTarea(
            pacienteId,
            TareaCrearRequestDto(titulo = titulo, descripcion = descripcion)
        ).toDomain()
    }

    override suspend fun marcarRealizada(tareaId: Long, realizada: Boolean): Result<Tarea> = runCatching {
        tareaApi.marcarRealizada(tareaId, TareaActualizarRealizadaRequestDto(realizada = realizada)).toDomain()
    }

    override suspend fun actualizarTarea(
        tareaId: Long,
        titulo: String,
        descripcion: String,
        realizada: Boolean
    ): Result<Tarea> = runCatching {
        tareaApi.actualizarTarea(
            tareaId,
            TareaActualizarRequestDto(titulo = titulo, descripcion = descripcion, realizada = realizada)
        ).toDomain()
    }

    override suspend fun eliminarTarea(tareaId: Long): Result<Unit> = runCatching {
        tareaApi.eliminarTarea(tareaId)
    }
}
