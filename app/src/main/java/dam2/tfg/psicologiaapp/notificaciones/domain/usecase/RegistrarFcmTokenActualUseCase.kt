package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository
import javax.inject.Inject

/**
 * Solicita el token FCM actual al SDK y lo registra en el backend.
 * Se invoca al iniciar sesión y al refrescar el token (onNewToken).
 */
class RegistrarFcmTokenActualUseCase @Inject constructor(
    private val notificacionesRepository: NotificacionesRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        val tokenResult = notificacionesRepository.obtenerTokenFcmActual()
        val token = tokenResult.getOrElse { return Result.failure(it) }
        return notificacionesRepository.registrarTokenActual(token)
    }

    /** Variante que recibe un token concreto (lo usa el FirebaseMessagingService.onNewToken). */
    suspend operator fun invoke(tokenConcreto: String): Result<Unit> =
        notificacionesRepository.registrarTokenActual(tokenConcreto)
}
