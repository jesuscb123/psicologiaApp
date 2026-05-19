package dam2.tfg.psicologiaapp.psicologo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "psicologos")
data class PsicologoEntity(
    @PrimaryKey val usuarioId: Long,
    val numeroColegiado: String,
    @ColumnInfo(name = "especialidad") val especialidades: List<String>,
    val idEntidadPsicologo: Long = 0L,
    val firebaseUid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val fotoPerfilUrl: String? = null,
    val descripcion: String? = null,
)
