package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository

open class FakeNotificacionesRepository : NotificacionesRepository {
    override suspend fun registrarTokenActual(token: String): Result<Unit> = Result.success(Unit)
    override suspend fun darDeBajaToken(token: String): Result<Unit> = Result.success(Unit)
    override suspend fun obtenerTokenFcmActual(): Result<String> = Result.success("token-fcm")
    override suspend fun notificarMensajeChat(chatId: String, vistaPreviaTexto: String): Result<Unit> = Result.success(Unit)
}
