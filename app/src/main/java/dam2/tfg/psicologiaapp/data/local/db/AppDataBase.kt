package dam2.tfg.psicologiaapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dam2.tfg.psicologiaapp.data.local.dao.UsuarioDao
import dam2.tfg.psicologiaapp.data.local.entity.UsuarioEntity

@Database(entities = [UsuarioEntity::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}