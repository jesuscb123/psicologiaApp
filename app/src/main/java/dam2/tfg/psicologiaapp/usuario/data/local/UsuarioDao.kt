package dam2.tfg.psicologiaapp.usuario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE usuarioId = :id")
    suspend fun obtenerPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE firebaseUid = :firebaseUid LIMIT 1")
    suspend fun obtenerPorFirebaseUid(firebaseUid: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios")
    suspend fun borrarTodos()
}

