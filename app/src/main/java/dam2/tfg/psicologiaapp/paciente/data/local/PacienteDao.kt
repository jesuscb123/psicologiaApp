package dam2.tfg.psicologiaapp.paciente.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes WHERE idPaciente = :idPaciente")
    suspend fun obtenerPorId(idPaciente: Long): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuarioId(usuarioId: Long): PacienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(paciente: PacienteEntity)

    @Query("DELETE FROM pacientes")
    suspend fun borrarTodos()
}

