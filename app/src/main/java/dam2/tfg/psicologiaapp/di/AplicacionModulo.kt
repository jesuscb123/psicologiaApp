package dam2.tfg.psicologiaapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Módulo raíz para dependencias de alto nivel.
 * Se irá completando a medida que se añadan casos de uso y repositorios.
 */
@Module
@InstallIn(SingletonComponent::class)
object AplicacionModulo

