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
}

