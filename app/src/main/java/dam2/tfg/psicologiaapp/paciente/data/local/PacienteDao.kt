package dam2.tfg.psicologiaapp.paciente.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes WHERE idPaciente = :idPaciente")
    suspend fun obtenerPorId(idPaciente: Long): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE idPaciente = :idPaciente")
    fun observarPorId(idPaciente: Long): Flow<PacienteEntity?>

    @Query("SELECT * FROM pacientes WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuarioId(usuarioId: Long): PacienteEntity?

    @Query("SELECT * FROM pacientes")
    fun observarTodos(): Flow<List<PacienteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(paciente: PacienteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(pacientes: List<PacienteEntity>)

    @Query("DELETE FROM pacientes")
    suspend fun borrarTodos()
}

