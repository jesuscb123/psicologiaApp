package dam2.tfg.psicologiaapp.core.di

import dam2.tfg.psicologiaapp.usuario.data.repository.UsuarioRepositoryImpl
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(
        impl: UsuarioRepositoryImpl
    ): UsuarioRepository
}