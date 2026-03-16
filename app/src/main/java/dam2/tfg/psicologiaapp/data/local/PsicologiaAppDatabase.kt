package dam2.tfg.psicologiaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteEntity
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoDao
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoEntity
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioEntity

/**
 * Base de datos principal de Room.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        PacienteEntity::class,
        PsicologoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PsicologiaAppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun pacienteDao(): PacienteDao

    abstract fun psicologoDao(): PsicologoDao
}

