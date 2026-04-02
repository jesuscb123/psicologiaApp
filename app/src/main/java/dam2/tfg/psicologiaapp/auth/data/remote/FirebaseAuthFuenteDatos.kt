package dam2.tfg.psicologiaapp.auth.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseAuthFuenteDatos @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun iniciarSesion(correo: String, contrasena: String): String =
        suspendCancellableCoroutine { continuación ->
            firebaseAuth
                .signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener { tarea ->
                    if (tarea.isSuccessful) {
                        val resultadoUid = tarea.result.user?.uid
                        if (resultadoUid != null) {
                            continuación.resume(resultadoUid)
                        } else {
                            continuación.resumeWithException(
                                IllegalStateException("FirebaseAuth devolvió usuario null tras iniciar sesión")
                            )
                        }
                    } else {
                        val excepción = tarea.exception ?: Exception("Error desconocido al iniciar sesión en Firebase")
                        continuación.resumeWithException(excepción)
                    }
                }
        }

    suspend fun crearCuenta(correo: String, contrasena: String): String =
        suspendCancellableCoroutine { continuación ->
            firebaseAuth
                .createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener { tarea ->
                    if (tarea.isSuccessful) {
                        val resultadoUid = tarea.result.user?.uid
                        if (resultadoUid != null) {
                            continuación.resume(resultadoUid)
                        } else {
                            continuación.resumeWithException(
                                IllegalStateException("FirebaseAuth devolvió usuario null tras crear cuenta")
                            )
                        }
                    } else {
                        val excepción = tarea.exception ?: Exception("Error desconocido al crear cuenta en Firebase")
                        continuación.resumeWithException(excepción)
                    }
                }
        }
}

