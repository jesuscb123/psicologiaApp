package dam2.tfg.psicologiaapp.usuario.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Retrofit para endpoints de usuarios del backend.
 * Base: /api/usuarios
 */
interface UsuarioApi {

    @GET("api/usuarios/existe-email")
    suspend fun existeCorreo(@Query("email") email: String): ExisteCorreoResponseDto

    @GET("api/usuarios/me")
    suspend fun getPerfilActual(): UsuarioPerfilResponseDto

    @POST("api/usuarios")
    suspend fun crearUsuario(@Body request: UsuarioRequestDto): UsuarioResponseDto

    @PATCH("api/usuarios/me/email")
    suspend fun actualizarEmail(@Body body: ActualizarEmailRequestDto): UsuarioPerfilResponseDto

    @Multipart
    @POST("api/usuarios/me/foto")
    suspend fun subirFotoPerfil(@Part archivo: MultipartBody.Part): UsuarioPerfilResponseDto

    @DELETE("api/usuarios/me")
    suspend fun borrarUsuario(): Unit

    @GET("api/usuarios/{fireBaseUid}")
    suspend fun obtenerUsuarioPorFirebase(@Path("fireBaseUid") fireBaseUid: String): UsuarioResponseDto
}
