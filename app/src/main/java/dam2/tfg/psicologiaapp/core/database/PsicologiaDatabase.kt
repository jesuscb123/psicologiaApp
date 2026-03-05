package dam2.tfg.psicologiaapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PsicologiaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}