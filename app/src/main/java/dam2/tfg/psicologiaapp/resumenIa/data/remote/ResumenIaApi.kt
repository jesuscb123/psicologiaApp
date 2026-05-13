package dam2.tfg.psicologiaapp.resumenIa.data.remote

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API Retrofit para el endpoint de resumen IA de notas de un paciente.
 * La generación es bajo demanda: el backend toma las últimas N notas
 * (anonimizadas) y delega a Groq.
 */
interface ResumenIaApi {

    @POST("api/notas/pacientes/{pacienteId}/resumen-ia")
    suspend fun generarResumenNotasPaciente(
        @Path("pacienteId") pacienteId: Long
    ): Response<ResumenIaResponseDto>
}
