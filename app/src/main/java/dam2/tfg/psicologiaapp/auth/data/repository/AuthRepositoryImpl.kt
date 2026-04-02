package dam2.tfg.psicologiaapp.auth.data.repository

import dam2.tfg.psicologiaapp.auth.data.remote.FirebaseAuthFuenteDatos
import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthFuenteDatos: FirebaseAuthFuenteDatos
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, contrasena: String): Result<String> = runCatching {
        firebaseAuthFuenteDatos.iniciarSesion(correo = correo, contrasena = contrasena)
    }

    override suspend fun crearCuenta(correo: String, contrasena: String): Result<String> = runCatching {
        firebaseAuthFuenteDatos.crearCuenta(correo = correo, contrasena = contrasena)
    }
}

