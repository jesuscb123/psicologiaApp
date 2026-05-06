package dam2.tfg.psicologiaapp.notificaciones.data.remote

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lee del SDK de Firebase la información necesaria para registrar el token FCM:
 *  - el propio token de mensajería (cambia con la rotación que hace el SDK),
 *  - el id de instalación, que sí es estable y nos permite limpiar tokens viejos del mismo
 *    dispositivo cuando rota.
 */
@Singleton
class FcmFuenteDatos @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseInstallations: FirebaseInstallations,
) {

    suspend fun obtenerTokenActual(): String =
        firebaseMessaging.token.await()

    suspend fun obtenerIdInstalacion(): String? = runCatching {
        firebaseInstallations.id.await()
    }.getOrNull()

    suspend fun borrarTokenLocal() {
        firebaseMessaging.deleteToken().await()
    }
}
