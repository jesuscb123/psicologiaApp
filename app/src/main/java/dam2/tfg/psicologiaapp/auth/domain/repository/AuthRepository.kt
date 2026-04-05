package dam2.tfg.psicologiaapp.auth.domain.repository

/**
 * Contrato de autenticación (Firebase Auth) en capa dominio.
 * La implementación vive en data y nunca debe ser llamada directamente desde ViewModels.
 */
interface AuthRepository {

    /**
     * Inicia sesión en Firebase con correo/contraseña.
     * @return uid del usuario autenticado.
     */
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<String>

    /**
     * Crea una cuenta en Firebase con correo/contraseña.
     * @return uid del usuario creado.
     */
    suspend fun crearCuenta(correo: String, contrasena: String): Result<String>

    /**
     * Elimina el usuario actual en Firebase (p. ej. rollback tras fallo al crear usuario en API).
     */
    suspend fun eliminarUsuarioActual(): Result<Unit>

    /**
     * Cierra la sesión actual en Firebase Auth.
     */
    suspend fun cerrarSesion(): Result<Unit>

    /**
     * Fuerza la renovación del idToken en Firebase (actualiza la caché local).
     * Útil antes de ráfagas HTTP tras Storage u otras operaciones que cargan el SDK.
     */
    suspend fun forzarRenovacionTokenIdentidad()
}

