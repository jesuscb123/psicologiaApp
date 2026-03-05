package dam2.tfg.psicologiaapp.core.utils.auth

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val auth: FirebaseAuth by lazy { Firebase.auth}

    private val signInClient = Identity.getSignInClient(context)

    suspend fun crearUsuarioConEmailPassword(email: String, password: String): String?{
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: Exception) {
            Log.e("AuthManager", "Error: ${e.message}")
            null
        }
    }

    suspend fun iniciarSesionConEmailPassword(email: String, password: String): Boolean{
        return try{
            auth.signInWithEmailAndPassword(email, password).await()
            true
        }catch (e: FirebaseAuthException){
            Log.e("AuthManager", "Error al iniciar sesion: ${e.errorCode}")
            false
        }catch (e: Exception){
            Log.e("Auth Manager", "Error desconocido: $e")
            false
        }
    }

    fun cerrarSesion(): Boolean {
        return try {
            auth.signOut()
           signInClient.signOut()
            Log.d("AuthManager", "Sesión cerrada correctamente.")
            true
        } catch (e: Exception) {
            Log.e("AuthManager", "Error al cerrar sesión", e)
            false
        }
    }

    suspend fun obtenerIdToken(): String? {
        return try {
            val mUser = auth.currentUser
            val tokenResult = mUser?.getIdToken(true)?.await()
            tokenResult?.token
        } catch (e: Exception) {
            Log.e("AuthManager", "Error al obtener IdToken: ${e.message}")
            null
        }
    }


    suspend fun eliminarUsuarioActual(): Boolean {
        return try {
            auth.currentUser?.delete()?.await()
            true
        } catch (e: Exception) {
            Log.e("AuthManager", "Error eliminando usuario tras fallo en DB", e)
            false
        }
    }


}