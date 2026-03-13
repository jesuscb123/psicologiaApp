package dam2.tfg.psicologiaapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

/**
 * Implementación de [ProveedorTokenFirebase] que obtiene el idToken
 * del usuario actual de Firebase Authentication.
 */
class FirebaseProveedorToken @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ProveedorTokenFirebase {

    override suspend fun obtenerToken(): String? = suspendCancellableCoroutine { continuación ->
        firebaseAuth.currentUser?.getIdToken(false)
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
