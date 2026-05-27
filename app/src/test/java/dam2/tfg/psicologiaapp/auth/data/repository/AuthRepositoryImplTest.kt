package dam2.tfg.psicologiaapp.auth.data.repository

import dam2.tfg.psicologiaapp.auth.data.remote.FirebaseAuthFuenteDatos
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthRepositoryImplTest {

    private val firebaseAuth = mock<FirebaseAuthFuenteDatos>()
    private val proveedorToken = mock<ProveedorTokenFirebase>()
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(firebaseAuth, proveedorToken)
    }

    @Test
    fun `iniciarSesion debe devolver uid cuando Firebase tiene exito`() = runTest {
        whenever(firebaseAuth.iniciarSesion("a@b.com", "pass")).thenReturn("uid-123")

        val resultado = repository.iniciarSesion("a@b.com", "pass")

        assertTrue(resultado.isSuccess)
        assertEquals("uid-123", resultado.getOrNull())
    }

    @Test
    fun `iniciarSesion debe propagar error de Firebase`() = runTest {
        whenever(firebaseAuth.iniciarSesion("a@b.com", "pass"))
            .thenThrow(IllegalStateException("Credenciales invalidas"))

        val resultado = repository.iniciarSesion("a@b.com", "pass")

        assertTrue(resultado.isFailure)
        assertEquals("Credenciales invalidas", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `crearCuenta debe devolver uid cuando Firebase tiene exito`() = runTest {
        whenever(firebaseAuth.crearCuenta("nuevo@test.com", "secret")).thenReturn("uid-nuevo")

        val resultado = repository.crearCuenta("nuevo@test.com", "secret")

        assertTrue(resultado.isSuccess)
        assertEquals("uid-nuevo", resultado.getOrNull())
    }

    @Test
    fun `solicitarRestablecerContrasena debe completar con exito`() = runTest {
        whenever(firebaseAuth.solicitarRestablecerContrasena("a@b.com")).thenReturn(Unit)

        val resultado = repository.solicitarRestablecerContrasena("a@b.com")

        assertTrue(resultado.isSuccess)
    }

    @Test
    fun `cerrarSesion debe delegar en Firebase`() = runTest {
        val resultado = repository.cerrarSesion()

        assertTrue(resultado.isSuccess)
        verify(firebaseAuth).cerrarSesion()
    }

    @Test
    fun `eliminarUsuarioActual debe propagar error si no hay usuario`() = runTest {
        whenever(firebaseAuth.eliminarUsuarioActual())
            .thenThrow(IllegalStateException("No hay usuario"))

        val resultado = repository.eliminarUsuarioActual()

        assertTrue(resultado.isFailure)
        assertEquals("No hay usuario", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `forzarRenovacionTokenIdentidad debe pedir token con renovacion`() = runTest {
        whenever(proveedorToken.obtenerToken(forzarRenovacion = true)).thenReturn("token-nuevo")

        repository.forzarRenovacionTokenIdentidad()

        verify(proveedorToken).obtenerToken(forzarRenovacion = true)
    }
}
