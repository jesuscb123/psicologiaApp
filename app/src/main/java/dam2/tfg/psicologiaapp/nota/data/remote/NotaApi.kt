package dam2.tfg.psicologiaapp.nota.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * API Retrofit para endpoints de notas del backend.
 */
interface NotaApi {

    @GET("api/notas")
    suspend fun getNotasPacienteActual(): Response<List<NotaResponseDto>>

    @GET("api/notas/pacientes/{pacienteId}")
    suspend fun getNotasDePaciente(@Path("pacienteId") pacienteId: Long): List<NotaResponseDto>

    @POST("api/notas")
    suspend fun crearNota(@Body body: NotaRequestDto): NotaResponseDto

    @PUT("api/notas/{notaId}")
    suspend fun actualizarNota(
        @Path("notaId") notaId: Long,
        @Body body: NotaRequestDto
    ): NotaResponseDto

    @DELETE("api/notas/{notaId}")
    suspend fun borrarNota(@Path("notaId") notaId: Long): Unit
}
