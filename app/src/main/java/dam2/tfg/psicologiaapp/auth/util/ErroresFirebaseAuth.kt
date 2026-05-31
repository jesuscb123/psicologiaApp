package dam2.tfg.psicologiaapp.auth.util

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Throwable.mensajeErrorCreacionCuenta(): String {
    when (this) {
        is FirebaseAuthUserCollisionException ->
            return "Este correo electrónico ya está registrado"
        is FirebaseAuthWeakPasswordException ->
            return "La contraseña es demasiado débil (mínimo 6 caracteres)"
        is FirebaseAuthInvalidCredentialsException ->
            return "El correo o la contraseña no son válidos"
    }
    val mensaje = message?.lowercase().orEmpty()
    return when {
        mensaje.contains("email address is already in use") ||
            mensaje.contains("already in use") ||
            mensaje.contains("email-already-in-use") ||
            mensaje.contains("email_already_in_use") ->
            "Este correo electrónico ya está registrado"
        mensaje.contains("weak password") || mensaje.contains("weak-password") ->
            "La contraseña es demasiado débil (mínimo 6 caracteres)"
        mensaje.contains("invalid email") || mensaje.contains("invalid-email") ->
            "El formato del correo no es válido"
        else -> message ?: "No se pudo crear la cuenta"
    }
}
