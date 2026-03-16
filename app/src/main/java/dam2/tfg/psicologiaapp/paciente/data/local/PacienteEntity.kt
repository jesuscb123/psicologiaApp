package dam2.tfg.psicologiaapp.paciente.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacientes")
data class PacienteEntity(
    @PrimaryKey val idPaciente: Long,
    val usuarioId: Long,
    val psicologoId: Long?
)

