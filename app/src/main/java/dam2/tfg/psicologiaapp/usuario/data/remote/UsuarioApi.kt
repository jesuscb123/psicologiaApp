package dam2.tfg.psicologiaapp.usuario.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API Retrofit para endpoints de usuarios del backend.
 * Base: /api/usuarios
 */
interface UsuarioApi {

    @GET("api/usuarios/me")
    suspend fun getPerfilActual(): UsuarioPerfilResponseDto

    @POST("api/usuarios")
    suspend fun crearUsuario(@Body request: UsuarioRequestDto): UsuarioResponseDto

    @PATCH("api/usuarios/me/email")
    suspend fun actualizarEmail(@Body body: ActualizarEmailRequestDto): UsuarioPerfilResponseDto

    @DELETE("api/usuarios/me")
    suspend fun borrarUsuario(): Unit

    @GET("api/usuarios/{fireBaseUid}")
    suspend fun obtenerUsuarioPorFirebase(@Path("fireBaseUid") fireBaseUid: String): UsuarioResponseDto
}
