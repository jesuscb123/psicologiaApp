package dam2.tfg.psicologiaapp.usuario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE usuarioId = :id")
    suspend fun obtenerPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE firebaseUid = :firebaseUid LIMIT 1")
    suspend fun obtenerPorFirebaseUid(firebaseUid: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios LIMIT 1")
    fun observarPrimero(): Flow<UsuarioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios")
    suspend fun borrarTodos()
}

