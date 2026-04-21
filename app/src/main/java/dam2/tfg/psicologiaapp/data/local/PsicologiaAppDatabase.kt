package dam2.tfg.psicologiaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dam2.tfg.psicologiaapp.nota.data.local.NotaDao
import dam2.tfg.psicologiaapp.nota.data.local.NotaEntity
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteEntity
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoDao
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoEntity
import dam2.tfg.psicologiaapp.tarea.data.local.TareaDao
import dam2.tfg.psicologiaapp.tarea.data.local.TareaEntity
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioEntity

/**
 * Base de datos principal de Room.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        PacienteEntity::class,
        PsicologoEntity::class,
        NotaEntity::class,
        TareaEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class PsicologiaAppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun pacienteDao(): PacienteDao

    abstract fun psicologoDao(): PsicologoDao

    abstract fun notaDao(): NotaDao

    abstract fun tareaDao(): TareaDao
}

