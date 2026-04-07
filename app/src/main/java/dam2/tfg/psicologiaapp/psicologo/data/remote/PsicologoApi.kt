package dam2.tfg.psicologiaapp.psicologo.data.remote

import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Retrofit para endpoints de psicólogos del backend.
 * Base: /api/psicologos
 */
interface PsicologoApi {

    @GET("api/psicologos")
    suspend fun listarPsicologos(): List<PsicologoResponseDto>

    @GET("api/psicologos/buscar")
    suspend fun buscarPsicologos(@Query("nombreUsuario") nombre: String): List<PsicologoResponseDto>

    @GET("api/psicologos/firebaseId/{firebaseId}")
    suspend fun getPsicologoPorFirebase(@Path("firebaseId") firebaseId: String): PsicologoResponseDto

    @GET("api/psicologos/me/pacientes")
    suspend fun getPacientesDePsicologo(): Response<List<PacienteResponseDto>>
}
