package dam2.tfg.psicologiaapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import dam2.tfg.psicologiaapp.BuildConfig
import dam2.tfg.psicologiaapp.data.remote.AuthTokenInterceptor
import dam2.tfg.psicologiaapp.data.remote.AuthTokenRefrescoAuthenticator
import dam2.tfg.psicologiaapp.data.remote.FirebaseProveedorToken
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.nota.data.remote.NotaApi
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
    }
}
