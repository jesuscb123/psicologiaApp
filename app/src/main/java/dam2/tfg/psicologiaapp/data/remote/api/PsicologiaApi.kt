package dam2.tfg.psicologiaapp.data.remote.api

import dam2.tfg.psicologiaapp.data.remote.dto.CrearUsuarioRequest
import dam2.tfg.psicologiaapp.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PsicologiaApi {
    @POST("/api/usuarios")
    suspend fun crearUsuario(
        @Header("Authorization") authHeader: String,
        @Body request: CrearUsuarioRequest
    ): UsuarioDto
    @GET("/api/usuarios/{firebaseUid}")
    suspend fun getUsuarioByFirebaseUid(@Path("firebaseUid") firebaseUid: String): UsuarioDto

    @GET("/api/usuarios")
    suspend fun getUsuariosRegistrados(): List<UsuarioDto>


}

