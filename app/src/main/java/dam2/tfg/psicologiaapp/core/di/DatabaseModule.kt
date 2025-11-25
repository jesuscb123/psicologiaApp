package dam2.tfg.psicologiaapp.core.di

import android.content.Context
import androidx.room.Room
import dam2.tfg.psicologiaapp.usuario.data.local.dao.UsuarioDao
import dam2.tfg.psicologiaapp.data.local.db.AppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "psicologia_db"
        ).build()

    @Provides
    @Singleton
    fun provideUsuarioDao(db: AppDataBase): UsuarioDao =
        db.usuarioDao()
}