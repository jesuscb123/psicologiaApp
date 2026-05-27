package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.*
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SincronizarPerfilActualUseCaseTest {

    @Test
    fun `invoke debe obtener perfil del repositorio y guardarlo en cache`() = runTest {
        val perfil = UsuarioPerfilBasico(1, "uid", "Nombre", "Apellidos", "email@test.com", null, RolUsuario.PACIENTE)
        var perfilGuardado: UsuarioPerfil? = null
        
        val repo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.success(perfil)
        }
        val cache = object : FakeUsuarioCacheRepository() {
            override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {
                perfilGuardado = perfil
            }
        }
        
        val useCase = SincronizarPerfilActualUseCase(repo, cache)
        val resultado = useCase()

        assertTrue(resultado.isSuccess)
        assertEquals(perfil, perfilGuardado)
    }

    @Test
    fun `invoke debe devolver fallo si el repositorio falla`() = runTest {
        val repo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.failure(Exception("Error de red"))
        }
        val cache = FakeUsuarioCacheRepository()
        
        val useCase = SincronizarPerfilActualUseCase(repo, cache)
        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Error de red", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke debe devolver fallo si la cache falla`() = runTest {
        val perfil = UsuarioPerfilBasico(1, "uid", "Nombre", "Apellidos", "email@test.com", null, RolUsuario.PACIENTE)
        val repo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.success(perfil)
        }
        val cache = object : FakeUsuarioCacheRepository() {
            override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {
                throw Exception("Error de base de datos")
            }
        }
        
        val useCase = SincronizarPerfilActualUseCase(repo, cache)
        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Error de base de datos", resultado.exceptionOrNull()?.message)
    }

    private open class FakeUsuarioRepository : UsuarioRepository {
        override suspend fun existeCorreo(email: String): Result<Boolean> = Result.success(true)
        override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
        override suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario> = Result.failure(NotImplementedError())
        override suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
        override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
        override suspend fun borrarUsuario(): Result<Unit> = Result.success(Unit)
        override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario> = Result.failure(NotImplementedError())
    }

    private open class FakeUsuarioCacheRepository : UsuarioCacheRepository {
        override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado? = null
        override fun observarPerfilCacheado(): Flow<PerfilCacheado?> = emptyFlow()
        override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {}
        override suspend fun limpiarCache() {}
    }
}
