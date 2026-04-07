package dam2.tfg.psicologiaapp.tarea.domain.repository

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de tareas en dominio.
 */
interface TareaRepository {

    suspend fun getTareasPacienteActual(): Result<List<Tarea>>

    suspend fun getTareasDePaciente(pacienteId: Long): Result<List<Tarea>>

    fun observarTareasPacienteActual(): Flow<List<Tarea>>

    fun observarTareasDePaciente(pacienteId: Long): Flow<List<Tarea>>

    suspend fun sincronizarTareasPacienteActual(): Result<Unit>

    suspend fun sincronizarTareasDePaciente(pacienteId: Long): Result<Unit>

    suspend fun crearTarea(pacienteId: Long, titulo: String, descripcion: String): Result<Tarea>

    suspend fun marcarRealizada(tareaId: Long, realizada: Boolean): Result<Tarea>

    suspend fun aceptarTarea(tareaId: Long): Result<Tarea>

    suspend fun actualizarTarea(
        tareaId: Long,
        titulo: String,
        descripcion: String,
        realizada: Boolean
    ): Result<Tarea>

    suspend fun eliminarTarea(tareaId: Long): Result<Unit>
}
