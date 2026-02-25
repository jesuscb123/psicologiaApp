package dam2.tfg.psicologiaapp.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dam2.tfg.psicologiaapp.core.database.PsicologiaDatabase
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePsicologiaDatabase(@ApplicationContext context: Context): PsicologiaDatabase {
        return Room.databaseBuilder(
            context,
            PsicologiaDatabase::class.java,
            "psicologia_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUsuarioDao(database: PsicologiaDatabase): UsuarioDao {
        return database.usuarioDao()
    }
}