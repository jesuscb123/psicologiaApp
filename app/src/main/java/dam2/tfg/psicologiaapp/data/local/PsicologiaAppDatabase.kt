package dam2.tfg.psicologiaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos principal de Room.
 * De momento no expone entidades reales; se añadirá la primera entidad
 * de dominio cuando se implemente una feature.
 */
@Database(
    entities = [],
    version = 1,
    exportSchema = true
)
abstract class PsicologiaAppDatabase : RoomDatabase()

