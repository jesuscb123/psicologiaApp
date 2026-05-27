package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakeTareaRepository : TareaRepository {
    override suspend fun getTareasPacienteActual(): Result<List<Tarea>> = Result.success(emptyList())
    override suspend fun getTareasDePaciente(pacienteId: Long): Result<List<Tarea>> = Result.success(emptyList())
    override fun observarTareasPacienteActual(): Flow<List<Tarea>> = emptyFlow()
    override fun observarTareasDePaciente(pacienteId: Long): Flow<List<Tarea>> = emptyFlow()
    override suspend fun sincronizarTareasPacienteActual(): Result<Unit> = Result.success(Unit)
    override suspend fun sincronizarTareasDePaciente(pacienteId: Long): Result<Unit> = Result.success(Unit)
    override suspend fun crearTarea(pacienteId: Long, titulo: String, descripcion: String): Result<Tarea> =
        Result.failure(NotImplementedError())
    override suspend fun marcarRealizada(tareaId: Long, realizada: Boolean): Result<Tarea> =
        Result.failure(NotImplementedError())
    override suspend fun aceptarTarea(tareaId: Long): Result<Tarea> = Result.failure(NotImplementedError())
    override suspend fun actualizarTarea(tareaId: Long, titulo: String, descripcion: String, realizada: Boolean): Result<Tarea> =
        Result.failure(NotImplementedError())
    override suspend fun eliminarTarea(tareaId: Long): Result<Unit> = Result.success(Unit)
}
