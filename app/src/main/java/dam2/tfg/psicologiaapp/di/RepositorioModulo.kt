package dam2.tfg.psicologiaapp.di

import dam2.tfg.psicologiaapp.nota.data.repository.NotaRepositoryImpl
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import dam2.tfg.psicologiaapp.paciente.data.repository.PacienteRepositoryImpl
import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.psicologo.data.repository.PsicologoRepositoryImpl
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import dam2.tfg.psicologiaapp.tarea.data.repository.TareaRepositoryImpl
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import dam2.tfg.psicologiaapp.usuario.data.repository.UsuarioRepositoryImpl
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositorioModulo {

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindPacienteRepository(impl: PacienteRepositoryImpl): PacienteRepository

    @Binds
    @Singleton
    abstract fun bindPsicologoRepository(impl: PsicologoRepositoryImpl): PsicologoRepository

    @Binds
    @Singleton
    abstract fun bindNotaRepository(impl: NotaRepositoryImpl): NotaRepository

    @Binds
    @Singleton
    abstract fun bindTareaRepository(impl: TareaRepositoryImpl): TareaRepository
}
