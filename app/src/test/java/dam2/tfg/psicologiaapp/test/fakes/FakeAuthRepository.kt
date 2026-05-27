package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository

open class FakeAuthRepository : AuthRepository {
    override suspend fun iniciarSesion(correo: String, contrasena: String): Result<String> =
        Result.failure(NotImplementedError())

    override suspend fun crearCuenta(correo: String, contrasena: String): Result<String> =
        Result.failure(NotImplementedError())

    override suspend fun eliminarUsuarioActual(): Result<Unit> = Result.success(Unit)

    override suspend fun cerrarSesion(): Result<Unit> = Result.success(Unit)

    override suspend fun solicitarRestablecerContrasena(correo: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun forzarRenovacionTokenIdentidad() {}
}
