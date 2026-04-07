package dam2.tfg.psicologiaapp.nota.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("SELECT * FROM notas ORDER BY id DESC")
    fun observarTodas(): Flow<List<NotaEntity>>

    @Query("SELECT * FROM notas WHERE pacienteId = :pacienteId ORDER BY id DESC")
    fun observarPorPacienteId(pacienteId: Long): Flow<List<NotaEntity>>

    @Query("SELECT * FROM notas ORDER BY id DESC")
    suspend fun listarTodas(): List<NotaEntity>

    @Query("SELECT * FROM notas WHERE pacienteId = :pacienteId ORDER BY id DESC")
    suspend fun listarPorPacienteId(pacienteId: Long): List<NotaEntity>

    @Query("SELECT * FROM notas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): NotaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(nota: NotaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodas(notas: List<NotaEntity>)

    @Query("DELETE FROM notas WHERE id = :id")
    suspend fun borrarPorId(id: Long)

    @Query("DELETE FROM notas WHERE pacienteId = :pacienteId")
    suspend fun borrarPorPacienteId(pacienteId: Long)

    @Query("DELETE FROM notas")
    suspend fun borrarTodas()
}

