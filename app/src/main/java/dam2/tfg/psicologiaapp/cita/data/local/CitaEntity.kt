package dam2.tfg.psicologiaapp.cita.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class CitaEntity(
    @PrimaryKey val id: Long,
    val inicio: String,
    val fin: String,
    val psicologoId: Long,
    val pacienteId: Long,
    val nombrePsicologo: String,
    val nombrePaciente: String,
    val estadoPersistido: String,
    val estadoCalculado: String,
    /** true = cargada desde el rol PACIENTE, false = desde el rol PSICOLOGO. */
    val esDePaciente: Boolean,
)
