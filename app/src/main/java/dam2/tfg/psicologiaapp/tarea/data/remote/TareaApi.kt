package dam2.tfg.psicologiaapp.tarea.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * API Retrofit para endpoints de tareas del backend.
 * Base: /api/tareas
 */
interface TareaApi {

    @GET("api/tareas")
    suspend fun getTareasPacienteActual(): List<TareaResponseDto>

    @GET("api/tareas/pacientes/{pacienteId}")
    suspend fun getTareasDePaciente(@Path("pacienteId") pacienteId: Long): List<TareaResponseDto>

    @POST("api/tareas/pacientes/{pacienteId}")
    suspend fun crearTarea(
        @Path("pacienteId") pacienteId: Long,
        @Body body: TareaCrearRequestDto
    ): TareaResponseDto

    @PATCH("api/tareas/{tareaId}/realizada")
    suspend fun marcarRealizada(
        @Path("tareaId") tareaId: Long,
        @Body body: TareaActualizarRealizadaRequestDto
    ): TareaResponseDto

    @PUT("api/tareas/{tareaId}")
    suspend fun actualizarTarea(
        @Path("tareaId") tareaId: Long,
        @Body body: TareaActualizarRequestDto
    ): TareaResponseDto

    @DELETE("api/tareas/{tareaId}")
    suspend fun eliminarTarea(@Path("tareaId") tareaId: Long): Unit
}
