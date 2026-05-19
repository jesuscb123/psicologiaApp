package dam2.tfg.psicologiaapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dam2.tfg.psicologiaapp.cita.data.local.CitaDao
import dam2.tfg.psicologiaapp.data.local.PsicologiaAppDatabase
import dam2.tfg.psicologiaapp.nota.data.local.NotaDao
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoDao
import dam2.tfg.psicologiaapp.tarea.data.local.TareaDao
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseDeDatosModulo {

    private val migracion_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notas (
                    id INTEGER NOT NULL PRIMARY KEY,
                    asunto TEXT NOT NULL,
                    descripcion TEXT NOT NULL,
                    pacienteId INTEGER NOT NULL,
                    psicologoId INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS tareas (
                    id INTEGER NOT NULL PRIMARY KEY,
                    titulo TEXT NOT NULL,
                    descripcion TEXT NOT NULL,
                    horaEnvio TEXT NOT NULL,
                    realizada INTEGER NOT NULL,
                    aceptadaPorPaciente INTEGER NOT NULL,
                    psicologoId INTEGER NOT NULL,
                    pacienteId INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val migracion_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS usuarios_nuevo (
                    usuarioId INTEGER NOT NULL PRIMARY KEY,
                    firebaseUid TEXT NOT NULL,
                    nombre TEXT NOT NULL,
                    apellidos TEXT NOT NULL,
                    fotoPerfilUrl TEXT,
                    rol TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO usuarios_nuevo (usuarioId, firebaseUid, nombre, apellidos, fotoPerfilUrl, rol)
                SELECT usuarioId, firebaseUid, nombreUsuario, '', fotoPerfilUrl, rol
                FROM usuarios
                """.trimIndent()
            )

            db.execSQL("DROP TABLE usuarios")
            db.execSQL("ALTER TABLE usuarios_nuevo RENAME TO usuarios")
        }
    }

    private val migracion_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE notas
                ADD COLUMN ultimaModificacion TEXT NOT NULL DEFAULT ''
                """.trimIndent()
            )
        }
    }

    private val migracion_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // usuarios: add email and psicologoId
            db.execSQL("ALTER TABLE usuarios ADD COLUMN email TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE usuarios ADD COLUMN psicologoId INTEGER")

            // pacientes: add extra fields
            db.execSQL("ALTER TABLE pacientes ADD COLUMN firebaseUid TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE pacientes ADD COLUMN nombre TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE pacientes ADD COLUMN apellidos TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE pacientes ADD COLUMN fotoPerfilUrl TEXT")

            // psicologos: add extra fields
            db.execSQL("ALTER TABLE psicologos ADD COLUMN idEntidadPsicologo INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE psicologos ADD COLUMN firebaseUid TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE psicologos ADD COLUMN nombre TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE psicologos ADD COLUMN apellidos TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE psicologos ADD COLUMN fotoPerfilUrl TEXT")
            db.execSQL("ALTER TABLE psicologos ADD COLUMN descripcion TEXT")

            // citas: new table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS citas (
                    id INTEGER NOT NULL PRIMARY KEY,
                    inicio TEXT NOT NULL,
                    fin TEXT NOT NULL,
                    psicologoId INTEGER NOT NULL,
                    pacienteId INTEGER NOT NULL,
                    nombrePsicologo TEXT NOT NULL,
                    nombrePaciente TEXT NOT NULL,
                    estadoPersistido TEXT NOT NULL,
                    estadoCalculado TEXT NOT NULL,
                    esDePaciente INTEGER NOT NULL DEFAULT 1
                )
                """.trimIndent()
            )
        }
    }

    private val migracion_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("UPDATE psicologos SET especialidad = '[]' WHERE especialidad = '' OR especialidad IS NULL")
            db.execSQL(
                "UPDATE psicologos SET especialidad = '[\"' || especialidad || '\"]' " +
                "WHERE especialidad != '' AND especialidad NOT LIKE '[%'"
            )
        }
    }

    @Provides
    @Singleton
    fun proporcionarBaseDeDatos(
        @ApplicationContext contextoAplicacion: Context
    ): PsicologiaAppDatabase =
        Room.databaseBuilder(
            contextoAplicacion,
            PsicologiaAppDatabase::class.java,
            "psicologia_app.db"
        )
            .addMigrations(migracion_1_2, migracion_2_3, migracion_3_4, migracion_4_5, migracion_5_6)
            .build()

    @Provides
    fun proporcionarNotaDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): NotaDao = baseDeDatos.notaDao()

    @Provides
    fun proporcionarTareaDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): TareaDao = baseDeDatos.tareaDao()

    @Provides
    fun proporcionarCitaDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): CitaDao = baseDeDatos.citaDao()

    @Provides
    fun proporcionarPacienteDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): PacienteDao = baseDeDatos.pacienteDao()

    @Provides
    fun proporcionarPsicologoDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): PsicologoDao = baseDeDatos.psicologoDao()

    @Provides
    fun proporcionarUsuarioDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): UsuarioDao = baseDeDatos.usuarioDao()
}

