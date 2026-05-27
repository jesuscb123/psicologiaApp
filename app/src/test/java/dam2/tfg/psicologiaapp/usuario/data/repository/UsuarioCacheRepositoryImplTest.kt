package dam2.tfg.psicologiaapp.usuario.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioEntity
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UsuarioCacheRepositoryImplTest {

    private lateinit var dao: FakeUsuarioDao
    private lateinit var repository: UsuarioCacheRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeUsuarioDao()
        repository = UsuarioCacheRepositoryImpl(dao)
    }

    @Test
    fun `obtenerPerfilCacheadoPorFirebaseUid debe devolver null si no hay datos`() = runTest {
        assertNull(repository.obtenerPerfilCacheadoPorFirebaseUid("uid-1"))
    }

    @Test
    fun `guardarDesdePerfil y obtener deben persistir perfil cacheado`() = runTest {
        val perfil = PacientePerfil(
            usuarioId = 1L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            email = "ana@test.com",
            fotoPerfilUrl = null,
            rol = RolUsuario.PACIENTE,
            psicologoId = 10L,
        )

        repository.guardarDesdePerfil(perfil)
        val cacheado = repository.obtenerPerfilCacheadoPorFirebaseUid("uid-pac")

        assertEquals(1L, cacheado?.usuarioId)
        assertEquals("Ana", cacheado?.nombre)
        assertEquals(RolUsuario.PACIENTE, cacheado?.rol)
        assertEquals(10L, cacheado?.psicologoId)
    }

    @Test
    fun `observarPerfilCacheado debe emitir cambios del dao`() = runTest {
        val perfil = PacientePerfil(
            usuarioId = 1L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            email = "ana@test.com",
            fotoPerfilUrl = null,
            rol = RolUsuario.PACIENTE,
            psicologoId = null,
        )

        repository.observarPerfilCacheado().test {
            assertNull(awaitItem())

            repository.guardarDesdePerfil(perfil)
            val cacheado = awaitItem()
            assertEquals("uid-pac", cacheado?.firebaseUid)

            repository.limpiarCache()
            assertNull(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `limpiarCache debe borrar todos los registros`() = runTest {
        val perfil = PacientePerfil(
            usuarioId = 1L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            email = "ana@test.com",
            fotoPerfilUrl = null,
            rol = RolUsuario.PACIENTE,
            psicologoId = null,
        )
        repository.guardarDesdePerfil(perfil)

        repository.limpiarCache()

        assertNull(repository.obtenerPerfilCacheadoPorFirebaseUid("uid-pac"))
        assertTrue(dao.entidades.isEmpty())
    }

    private class FakeUsuarioDao : UsuarioDao {
        val entidades = mutableListOf<UsuarioEntity>()
        private val state = MutableStateFlow<List<UsuarioEntity>>(emptyList())

        override suspend fun obtenerPorId(id: Long): UsuarioEntity? =
            entidades.firstOrNull { it.usuarioId == id }

        override suspend fun obtenerPorFirebaseUid(firebaseUid: String): UsuarioEntity? =
            entidades.firstOrNull { it.firebaseUid == firebaseUid }

        override fun observarPrimero(): Flow<UsuarioEntity?> =
            state.map { it.firstOrNull() }

        override suspend fun guardar(usuario: UsuarioEntity) {
            entidades.removeAll { it.usuarioId == usuario.usuarioId }
            entidades.add(usuario)
            state.value = entidades.toList()
        }

        override suspend fun borrarTodos() {
            entidades.clear()
            state.value = emptyList()
        }
    }
}
