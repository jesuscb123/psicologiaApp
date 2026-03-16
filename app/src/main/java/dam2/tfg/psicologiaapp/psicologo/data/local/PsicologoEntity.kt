package dam2.tfg.psicologiaapp.psicologo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "psicologos")
data class PsicologoEntity(
    @PrimaryKey val usuarioId: Long,
    val numeroColegiado: String,
    val especialidad: String
)

