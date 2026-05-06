package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.NotificacionesRepository
import javax.inject.Inject

/**
 * Da de baja el token FCM actual en el backend antes de cerrar sesión.
 * Si falla, no se debe abortar el logout: el repositorio devuelve [Result] para que
 * el llamador decida ignorar el fallo y continuar.
 */
class DarDeBajaFcmTokenUseCase @Inject constructor(
    private val notificacionesRepository: NotificacionesRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        val tokenResult = notificacionesRepository.obtenerTokenFcmActual()
        val token = tokenResult.getOrElse { return Result.failure(it) }
        return notificacionesRepository.darDeBajaToken(token)
    }
}
