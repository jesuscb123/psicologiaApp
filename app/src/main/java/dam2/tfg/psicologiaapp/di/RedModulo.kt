package dam2.tfg.psicologiaapp.di

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import dam2.tfg.psicologiaapp.BuildConfig
import dam2.tfg.psicologiaapp.data.remote.AuthTokenInterceptor
import dam2.tfg.psicologiaapp.data.remote.AuthTokenRefrescoAuthenticator
import dam2.tfg.psicologiaapp.data.remote.FirebaseProveedorToken
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.chat.data.remote.ChatApi
import dam2.tfg.psicologiaapp.cita.data.remote.CitaApi
import dam2.tfg.psicologiaapp.nota.data.remote.NotaApi
import dam2.tfg.psicologiaapp.notificaciones.data.remote.NotificacionesApi
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteApi
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoApi
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaApi
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioApi
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RedModulo {

    @Binds
    @Singleton
    abstract fun bindProveedorToken(impl: FirebaseProveedorToken): ProveedorTokenFirebase

    companion object {

        @Provides
        @Singleton
        fun proporcionarFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun proporcionarFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

        @Provides
        @Singleton
        fun proporcionarFirebaseInstallations(): FirebaseInstallations =
            FirebaseInstallations.getInstance()

        @Provides
        @Singleton
        fun proporcionarFirebaseDatabase(): FirebaseDatabase {
            val urlExplicita = BuildConfig.FIREBASE_RTDB_URL.trim().takeIf { it.isNotEmpty() }
            val urlDesdeGoogleServices = FirebaseApp.getInstance()
                .options
                .databaseUrl
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
            val url = urlExplicita ?: urlDesdeGoogleServices
            val db = if (url != null) {
                FirebaseDatabase.getInstance(url)
            } else {
                FirebaseDatabase.getInstance()
            }
            return db.apply { setPersistenceEnabled(true) }
        }

        @Provides
        @Singleton
        fun proporcionarGson(): com.google.gson.Gson = GsonBuilder()
            .registerTypeAdapter(UsuarioResponseDto::class.java, UsuarioResponseDtoDeserializer)
            .registerTypeAdapter(UsuarioPerfilResponseDto::class.java, UsuarioPerfilResponseDtoDeserializer)
            .registerTypeAdapter(UsuarioRequestDto::class.java, UsuarioRequestDtoSerializer)
            .create()

        @Provides
        @Singleton
        fun proporcionarOkHttpClient(
            authInterceptor: AuthTokenInterceptor,
            authTokenRefrescoAuthenticator: AuthTokenRefrescoAuthenticator
        ): OkHttpClient = OkHttpClient.Builder()
            .authenticator(authTokenRefrescoAuthenticator)
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        @Provides
        @Singleton
        fun proporcionarRetrofit(
            okHttpClient: OkHttpClient,
            gson: com.google.gson.Gson
        ): Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        @Provides
        @Singleton
        fun proporcionarUsuarioApi(retrofit: Retrofit): UsuarioApi =
            retrofit.create(UsuarioApi::class.java)

        @Provides
        @Singleton
        fun proporcionarPacienteApi(retrofit: Retrofit): PacienteApi =
            retrofit.create(PacienteApi::class.java)

        @Provides
        @Singleton
        fun proporcionarPsicologoApi(retrofit: Retrofit): PsicologoApi =
            retrofit.create(PsicologoApi::class.java)

        @Provides
        @Singleton
        fun proporcionarTareaApi(retrofit: Retrofit): TareaApi =
            retrofit.create(TareaApi::class.java)

        @Provides
        @Singleton
        fun proporcionarNotaApi(retrofit: Retrofit): NotaApi =
            retrofit.create(NotaApi::class.java)

        @Provides
        @Singleton
        fun proporcionarCitaApi(retrofit: Retrofit): CitaApi =
            retrofit.create(CitaApi::class.java)

        @Provides
        @Singleton
        fun proporcionarChatApi(retrofit: Retrofit): ChatApi =
            retrofit.create(ChatApi::class.java)

        @Provides
        @Singleton
        fun proporcionarNotificacionesApi(retrofit: Retrofit): NotificacionesApi =
            retrofit.create(NotificacionesApi::class.java)
    }
}
