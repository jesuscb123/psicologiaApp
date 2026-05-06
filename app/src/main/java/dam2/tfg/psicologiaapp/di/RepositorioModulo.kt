package dam2.tfg.psicologiaapp.di

import dam2.tfg.psicologiaapp.auth.data.repository.AuthRepositoryImpl
import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import dam2.tfg.psicologiaapp.chat.data.repository.ChatRepositoryImpl
import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import dam2.tfg.psicologiaapp.cita.data.repository.CitaRepositoryImpl
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import dam2.tfg.psicologiaapp.nota.data.repository.NotaRepositoryImpl
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import dam2.tfg.psicologiaapp.notificaciones.data.repository.NotificacionesRepositoryImpl
import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository
import dam2.tfg.psicologiaapp.paciente.data.repository.PacienteRepositoryImpl
import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.psicologo.data.repository.PsicologoRepositoryImpl
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import dam2.tfg.psicologiaapp.tarea.data.repository.TareaRepositoryImpl
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import dam2.tfg.psicologiaapp.preferencias.data.repository.TemaPreferenciasRepositoryImpl
import dam2.tfg.psicologiaapp.preferencias.domain.repository.TemaPreferenciasRepository
import dam2.tfg.psicologiaapp.usuario.data.repository.UsuarioRepositoryImpl
import dam2.tfg.psicologiaapp.usuario.data.repository.UsuarioCacheRepositoryImpl
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindUsuarioCacheRepository(impl: UsuarioCacheRepositoryImpl): UsuarioCacheRepository

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

    @Binds
    @Singleton
    abstract fun bindCitaRepository(impl: CitaRepositoryImpl): CitaRepository

    @Binds
    @Singleton
    abstract fun bindTemaPreferenciasRepository(
        impl: TemaPreferenciasRepositoryImpl,
    ): TemaPreferenciasRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindNotificacionesRepository(
        impl: NotificacionesRepositoryImpl,
    ): NotificacionesRepository
}
