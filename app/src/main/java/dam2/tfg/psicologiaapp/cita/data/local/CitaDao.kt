package dam2.tfg.psicologiaapp.cita.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    @Query("SELECT * FROM citas WHERE esDePaciente = 1 ORDER BY inicio ASC")
    fun observarCitasPaciente(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE esDePaciente = 0 ORDER BY inicio ASC")
    fun observarCitasPsicologo(): Flow<List<CitaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodas(citas: List<CitaEntity>)

    @Query("DELETE FROM citas WHERE esDePaciente = 1")
    suspend fun borrarCitasPaciente()

    @Query("DELETE FROM citas WHERE esDePaciente = 0")
    suspend fun borrarCitasPsicologo()

    @Query("DELETE FROM citas WHERE id = :citaId")
    suspend fun borrarPorId(citaId: Long)
}
