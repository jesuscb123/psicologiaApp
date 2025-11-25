package dam2.tfg.psicologiaapp.data.remote.api

import dam2.tfg.psicologiaapp.data.remote.dto.UsuarioDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PsicologiaApi {
    @GET("usuarios/{firebaseUid}")
    suspend fun getUsuarioByFirebaseUid(@Path("firebaseUid") firebaseUid: String): UsuarioDto
}

