package dam2.tfg.psicologiaapp.nota.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class NotaEntity(
    @PrimaryKey val id: Long,
    val asunto: String,
    val descripcion: String,
    val ultimaModificacion: String,
    val pacienteId: Long,
    val psicologoId: Long,
)

