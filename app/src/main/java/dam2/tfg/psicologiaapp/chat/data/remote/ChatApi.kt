package dam2.tfg.psicologiaapp.chat.data.remote

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {

    /** Called by a PACIENTE to obtain (or create) the chat with their assigned psychologist. */
    @POST("api/chats/me/psicologo")
    suspend fun asegurarChatPaciente(): Response<ChatResponseDto>

    /** Called by a PSICOLOGO to obtain (or create) the chat with one of their patients. */
    @POST("api/chats/pacientes/{pacienteId}")
    suspend fun asegurarChatPsicologo(
        @Path("pacienteId") pacienteId: Long,
    ): Response<ChatResponseDto>
}
