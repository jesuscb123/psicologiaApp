package dam2.tfg.psicologiaapp.psicologo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PsicologoDao {

    @Query("SELECT * FROM psicologos WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuarioId(usuarioId: Long): PsicologoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(psicologo: PsicologoEntity)

    @Query("DELETE FROM psicologos")
    suspend fun borrarTodos()
}

