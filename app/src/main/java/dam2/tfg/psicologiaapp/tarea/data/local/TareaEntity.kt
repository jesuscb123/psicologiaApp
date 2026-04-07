package dam2.tfg.psicologiaapp.tarea.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey val id: Long,
    val titulo: String,
    val descripcion: String,
    val horaEnvio: String,
    val realizada: Boolean,
    val aceptadaPorPaciente: Boolean,
    val psicologoId: Long,
    val pacienteId: Long,
)

