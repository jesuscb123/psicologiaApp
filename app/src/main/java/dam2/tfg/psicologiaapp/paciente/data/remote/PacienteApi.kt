package dam2.tfg.psicologiaapp.paciente.data.remote

import dam2.tfg.psicologiaapp.paciente.data.remote.AsignarPsicologoRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Retrofit para endpoints de pacientes del backend.
 * Base: /api/pacientes
 */
interface PacienteApi {

    @GET("api/pacientes")
    suspend fun listarPacientes(): List<PacienteResponseDto>

    @GET("api/pacientes/buscar")
    suspend fun buscarPacientes(@Query("nombreUsuario") nombre: String): List<PacienteResponseDto>

    @GET("api/pacientes/firebaseId/{firebaseId}")
    suspend fun getPacientePorFirebase(@Path("firebaseId") firebaseId: String): PacienteResponseDto

    @PATCH("api/pacientes/me/psicologo")
    suspend fun asignarPsicologo(@Body body: AsignarPsicologoRequestDto): PacienteResponseDto
}
