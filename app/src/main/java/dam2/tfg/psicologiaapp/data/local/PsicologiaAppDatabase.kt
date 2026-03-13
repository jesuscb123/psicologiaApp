package dam2.tfg.psicologiaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos principal de Room.
 * PlaceholderEntity es temporal; sustituir por entidades reales cuando se implementen.
 */
@Database(
    entities = [PlaceholderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PsicologiaAppDatabase : RoomDatabase()

