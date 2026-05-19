package dam2.tfg.psicologiaapp.psicologo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PsicologoDao {

    @Query("SELECT * FROM psicologos WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuarioId(usuarioId: Long): PsicologoEntity?

    @Query("SELECT * FROM psicologos")
    fun observarTodos(): Flow<List<PsicologoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(psicologo: PsicologoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(psicologos: List<PsicologoEntity>)

    @Query("DELETE FROM psicologos")
    suspend fun borrarTodos()
}

