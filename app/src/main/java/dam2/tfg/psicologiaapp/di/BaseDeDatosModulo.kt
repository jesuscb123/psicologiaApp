package dam2.tfg.psicologiaapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dam2.tfg.psicologiaapp.data.local.PsicologiaAppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseDeDatosModulo {

    @Provides
    @Singleton
    fun proporcionarBaseDeDatos(
        @ApplicationContext contextoAplicacion: Context
    ): PsicologiaAppDatabase =
        Room.databaseBuilder(
            contextoAplicacion,
            PsicologiaAppDatabase::class.java,
            "psicologia_app.db"
        ).build()
}

