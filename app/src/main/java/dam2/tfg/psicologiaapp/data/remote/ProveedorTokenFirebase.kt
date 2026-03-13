package dam2.tfg.psicologiaapp.data.remote

/**
 * Proveedor del token de Firebase para autenticación con el backend.
 * La implementación obtiene el idToken del usuario actual de Firebase Auth.
 */
interface ProveedorTokenFirebase {

    /**
     * Obtiene el token de Firebase del usuario actual.
     * @return El idToken si hay usuario autenticado, null en caso contrario.
     */
    suspend fun obtenerToken(): String?
}
