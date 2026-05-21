package dam2.tfg.psicologiaapp.chat.data.repository

import dam2.tfg.psicologiaapp.chat.data.remote.ChatApi
import dam2.tfg.psicologiaapp.chat.data.remote.ChatFuenteDatosFirebase
import dam2.tfg.psicologiaapp.chat.data.remote.toDomain
import dam2.tfg.psicologiaapp.chat.domain.model.Chat
import dam2.tfg.psicologiaapp.chat.domain.model.MensajeChat
import dam2.tfg.psicologiaapp.chat.domain.repository.ChatRepository
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val chatFuenteDatosFirebase: ChatFuenteDatosFirebase,
    private val proveedorTokenFirebase: ProveedorTokenFirebase,
) : ChatRepository {

    override suspend fun asegurarChatPaciente(): Result<Chat> = runCatching {
        val respuesta = ejecutarConRefrescoTokenSi401 { chatApi.asegurarChatPaciente() }
        if (!respuesta.isSuccessful) {
            val detalle = when (respuesta.code()) {
                401 -> "HTTP 401: token no aceptado"
                403 -> "HTTP 403: el usuario no tiene rol PACIENTE o no tiene psicólogo asignado"
                404 -> "HTTP 404: no se encontró psicólogo asignado"
                else -> "HTTP ${respuesta.code()}"
            }
            throw IllegalStateException("Error al asegurar chat de paciente: $detalle")
        }
        respuesta.body()?.toDomain()
            ?: throw IllegalStateException("Respuesta vacía al asegurar chat de paciente")
    }

    override suspend fun asegurarChatPsicologo(pacienteId: Long): Result<Chat> = runCatching {
        val respuesta = ejecutarConRefrescoTokenSi401 {
            chatApi.asegurarChatPsicologo(pacienteId)
        }
        if (!respuesta.isSuccessful) {
            val detalle = when (respuesta.code()) {
                401 -> "HTTP 401: token no aceptado"
                403 -> "HTTP 403: el usuario no tiene rol PSICOLOGO o el paciente no le pertenece"
                404 -> "HTTP 404: paciente no encontrado"
                else -> "HTTP ${respuesta.code()}"
            }
            throw IllegalStateException("Error al asegurar chat de psicólogo: $detalle")
        }
        respuesta.body()?.toDomain()
            ?: throw IllegalStateException("Respuesta vacía al asegurar chat de psicólogo")
    }

    override fun observarMensajes(rtdbRuta: String): Flow<List<MensajeChat>> =
        chatFuenteDatosFirebase.observarMensajes(rtdbRuta)

    override suspend fun enviarMensaje(rtdbRuta: String, texto: String): Result<Unit> = try {
        chatFuenteDatosFirebase.enviarMensaje(rtdbRuta, texto)
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }

    override suspend fun marcarChatLeido(rtdbRuta: String): Result<Unit> = try {
        chatFuenteDatosFirebase.marcarLeido(rtdbRuta)
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }

    override fun observarNoLeidosChatIds(miUid: String): Flow<Set<String>> =
        chatFuenteDatosFirebase.observarNoLeidosChatIds(miUid)

    private suspend fun <T> ejecutarConRefrescoTokenSi401(
        bloque: suspend () -> retrofit2.Response<T>,
    ): retrofit2.Response<T> {
        var respuesta = bloque()
        if (respuesta.code() == 401) {
            proveedorTokenFirebase.obtenerToken(forzarRenovacion = true)
            respuesta = bloque()
        }
        return respuesta
    }
}
