package dam2.tfg.psicologiaapp.auth.data.repository

import dam2.tfg.psicologiaapp.auth.data.remote.FirebaseAuthFuenteDatos
import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthFuenteDatos: FirebaseAuthFuenteDatos,
    private val proveedorTokenFirebase: ProveedorTokenFirebase,
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, contrasena: String): Result<String> = runCatching {
        firebaseAuthFuenteDatos.iniciarSesion(correo = correo, contrasena = contrasena)
    }

    override suspend fun crearCuenta(correo: String, contrasena: String): Result<String> = runCatching {
        firebaseAuthFuenteDatos.crearCuenta(correo = correo, contrasena = contrasena)
    }

    override suspend fun solicitarRestablecerContrasena(correo: String): Result<Unit> = runCatching {
        firebaseAuthFuenteDatos.solicitarRestablecerContrasena(correo = correo)
    }

    override suspend fun eliminarUsuarioActual(): Result<Unit> = runCatching {
        firebaseAuthFuenteDatos.eliminarUsuarioActual()
    }

    override suspend fun cerrarSesion(): Result<Unit> = runCatching {
        firebaseAuthFuenteDatos.cerrarSesion()
    }

    override suspend fun forzarRenovacionTokenIdentidad() {
        proveedorTokenFirebase.obtenerToken(forzarRenovacion = true)
    }
}

