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
import dam2.tfg.psicologiaapp.data.local.PsicologiaAppDatabase
import dam2.tfg.psicologiaapp.nota.data.local.NotaDao
import dam2.tfg.psicologiaapp.tarea.data.local.TareaDao
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
            .addMigrations(migracion_1_2)
            .build()

    @Provides
    fun proporcionarNotaDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): NotaDao = baseDeDatos.notaDao()

    @Provides
    fun proporcionarTareaDao(
        baseDeDatos: PsicologiaAppDatabase,
    ): TareaDao = baseDeDatos.tareaDao()
}

