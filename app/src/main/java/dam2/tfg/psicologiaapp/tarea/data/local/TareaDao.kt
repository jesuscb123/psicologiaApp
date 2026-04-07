package dam2.tfg.psicologiaapp.tarea.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun observarTodas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE pacienteId = :pacienteId ORDER BY id DESC")
    fun observarPorPacienteId(pacienteId: Long): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas ORDER BY id DESC")
    suspend fun listarTodas(): List<TareaEntity>

    @Query("SELECT * FROM tareas WHERE pacienteId = :pacienteId ORDER BY id DESC")
    suspend fun listarPorPacienteId(pacienteId: Long): List<TareaEntity>

    @Query("SELECT * FROM tareas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): TareaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(tarea: TareaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodas(tareas: List<TareaEntity>)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun borrarPorId(id: Long)

    @Query("DELETE FROM tareas WHERE pacienteId = :pacienteId")
    suspend fun borrarPorPacienteId(pacienteId: Long)

    @Query("DELETE FROM tareas")
    suspend fun borrarTodas()
}

