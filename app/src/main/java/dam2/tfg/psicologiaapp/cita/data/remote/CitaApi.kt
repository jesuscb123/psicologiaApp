package dam2.tfg.psicologiaapp.cita.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Retrofit para endpoints de citas del backend.
 * Base: /api/citas
 */
interface CitaApi {

    @GET("api/citas/disponibilidad")
    suspend fun getDisponibilidadDia(
        @Query("fecha") fechaIso: String,
        @Query("zonaHoraria") zonaHoraria: String,
    ): Response<DisponibilidadResponseDto>

    @POST("api/citas")
    suspend fun reservarCita(@Body body: CitaCrearRequestDto): Response<CitaResponseDto>

    @GET("api/citas")
    suspend fun getMisCitasPaciente(): Response<List<CitaResponseDto>>

    @GET("api/citas/psicologo")
    suspend fun getMisCitasPsicologo(): Response<List<CitaResponseDto>>

    @PATCH("api/citas/{citaId}/cancelar")
    suspend fun cancelarCita(@Path("citaId") citaId: Long): Response<CitaResponseDto>
}

