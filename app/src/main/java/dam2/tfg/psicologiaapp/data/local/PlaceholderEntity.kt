package dam2.tfg.psicologiaapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad placeholder para que Room permita tener la base de datos.
 * Eliminar cuando existan entidades reales (UsuarioEntity, NotaEntity, etc.).
 */
@Entity(tableName = "placeholder")
data class PlaceholderEntity(
    @PrimaryKey val id: Long = 0
)
