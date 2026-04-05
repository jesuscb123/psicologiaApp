package dam2.tfg.psicologiaapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.preferenciasPsicologiaDataStore by preferencesDataStore(
    name = "psicologia_preferencias"
)

@Module
@InstallIn(SingletonComponent::class)
object PreferenciasDataStoreModulo {

    @Provides
    @Singleton
    fun proporcionarPreferenciasDataStore(
        @ApplicationContext contextoAplicacion: Context
    ): DataStore<Preferences> = contextoAplicacion.preferenciasPsicologiaDataStore
}
