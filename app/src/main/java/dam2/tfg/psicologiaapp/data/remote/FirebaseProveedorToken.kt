package dam2.tfg.psicologiaapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

/**
 * Implementación de [ProveedorTokenFirebase] que obtiene el idToken
 * del usuario actual de Firebase Authentication.
 *
 * [mutexToken] serializa [getIdToken] para evitar carreras cuando OkHttp y corrutinas
 * piden el token en paralelo (p. ej. tras subir foto a Storage).
 */
class FirebaseProveedorToken @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ProveedorTokenFirebase {

    private val mutexToken = Mutex()

    override suspend fun obtenerToken(forzarRenovacion: Boolean): String? = mutexToken.withLock {
        suspendCancellableCoroutine { continuación ->
            firebaseAuth.currentUser?.getIdToken(forzarRenovacion)
                ?.addOnCompleteListener { tarea ->
                    if (tarea.isSuccessful) {
                        continuación.resume(tarea.result?.token)
                    } else {
                        continuación.resume(null)
                    }
                }
                ?: continuación.resume(null)
        }
    }
}
