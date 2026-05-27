package dam2.tfg.psicologiaapp.usuario.data.repository

import dam2.tfg.psicologiaapp.paciente.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.usuario.data.remote.ActualizarEmailRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.ExisteCorreoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioApi
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioSinRol
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UsuarioRepositoryImplTest {

    private val api = FakeUsuarioApi()
    private lateinit var repository: UsuarioRepositoryImpl

    @Before
    fun setUp() {
        repository = UsuarioRepositoryImpl(api)
    }

    @Test
    fun `existeCorreo debe devolver valor de la API`() = runTest {
        api.respuestaExisteCorreo = ExisteCorreoResponseDto(existe = true)

        val resultado = repository.existeCorreo("test@mail.com")

        assertTrue(resultado.isSuccess)
        assertEquals(true, resultado.getOrNull())
        assertEquals("test@mail.com", api.ultimoEmailConsultado)
    }

    @Test
    fun `getPerfilActual debe mapear respuesta a dominio`() = runTest {
        api.respuestaPerfil = perfilDto

        val resultado = repository.getPerfilActual()

        assertTrue(resultado.isSuccess)
        val perfil = resultado.getOrNull() as UsuarioPerfilBasico
        assertEquals(1L, perfil.usuarioId)
        assertEquals("ana@test.com", perfil.email)
    }

    @Test
    fun `crearUsuario debe mapear respuesta a dominio`() = runTest {
        api.respuestaCrear = UsuarioBasicoResponseDto(
            id = 5L,
            firebaseUid = "uid-nuevo",
            nombre = "Nuevo",
            apellidos = "Usuario",
            fotoPerfilUrl = null,
            rol = "PACIENTE",
        )

        val resultado = repository.crearUsuario(
            PacienteRequest(
                nombre = "Nuevo",
                apellidos = "Usuario",
                fotoPerfilUrl = null,
                psicologoId = null,
            ),
        )

        assertTrue(resultado.isSuccess)
        assertEquals(
            UsuarioSinRol(
                usuarioId = 5L,
                firebaseUid = "uid-nuevo",
                nombre = "Nuevo",
                apellidos = "Usuario",
                fotoPerfilUrl = null,
                rol = RolUsuario.PACIENTE,
            ),
            resultado.getOrNull(),
        )
    }

    @Test
    fun `actualizarEmail debe devolver perfil actualizado`() = runTest {
        api.respuestaActualizarEmail = perfilDto.copy(email = "nuevo@test.com")

        val resultado = repository.actualizarEmail("nuevo@test.com")

        assertTrue(resultado.isSuccess)
        assertEquals("nuevo@test.com", (resultado.getOrNull() as UsuarioPerfilBasico).email)
        assertEquals(ActualizarEmailRequestDto("nuevo@test.com"), api.ultimoActualizarEmail)
    }

    @Test
    fun `borrarUsuario debe completar con exito`() = runTest {
        val resultado = repository.borrarUsuario()

        assertTrue(resultado.isSuccess)
        assertTrue(api.borrarLlamado)
    }

    @Test
    fun `obtenerUsuarioPorFirebase debe propagar error de la API`() = runTest {
        api.errorObtenerPorFirebase = IllegalStateException("No encontrado")

        val resultado = repository.obtenerUsuarioPorFirebase("uid-desconocido")

        assertTrue(resultado.isFailure)
        assertEquals("No encontrado", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `subirFotoPerfil debe mapear respuesta con foto`() = runTest {
        api.respuestaSubirFoto = perfilDto.copy(fotoPerfilUrl = "https://cdn/foto.jpg")

        val resultado = repository.subirFotoPerfil(byteArrayOf(1, 2, 3), "image/jpeg")

        assertTrue(resultado.isSuccess)
        assertEquals("https://cdn/foto.jpg", resultado.getOrNull()?.fotoPerfilUrl)
        assertTrue(api.subirFotoLlamado)
    }

    private val perfilDto = UsuarioPerfilBasicoResponseDto(
        id = 1L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        email = "ana@test.com",
        fotoPerfilUrl = null,
        rol = "PACIENTE",
    )

    private class FakeUsuarioApi : UsuarioApi {
        var respuestaExisteCorreo = ExisteCorreoResponseDto(existe = false)
        var respuestaPerfil = UsuarioPerfilBasicoResponseDto(
            id = 0L,
            firebaseUid = "",
            nombre = "",
            apellidos = "",
            email = "",
            fotoPerfilUrl = null,
            rol = "PACIENTE",
        )
        var respuestaCrear = UsuarioBasicoResponseDto(
            id = 0L,
            firebaseUid = "",
            nombre = "",
            apellidos = "",
            fotoPerfilUrl = null,
            rol = "PACIENTE",
        )
        var respuestaActualizarEmail = respuestaPerfil
        var respuestaSubirFoto = respuestaPerfil
        var errorObtenerPorFirebase: Throwable? = null
        var ultimoEmailConsultado: String? = null
        var ultimoActualizarEmail: ActualizarEmailRequestDto? = null
        var borrarLlamado = false
        var subirFotoLlamado = false

        override suspend fun existeCorreo(email: String): ExisteCorreoResponseDto {
            ultimoEmailConsultado = email
            return respuestaExisteCorreo
        }

        override suspend fun getPerfilActual() = respuestaPerfil

        override suspend fun crearUsuario(request: dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto) =
            respuestaCrear

        override suspend fun actualizarEmail(body: ActualizarEmailRequestDto): UsuarioPerfilBasicoResponseDto {
            ultimoActualizarEmail = body
            return respuestaActualizarEmail
        }

        override suspend fun subirFotoPerfil(archivo: MultipartBody.Part): UsuarioPerfilBasicoResponseDto {
            subirFotoLlamado = true
            return respuestaSubirFoto
        }

        override suspend fun borrarUsuario() {
            borrarLlamado = true
        }

        override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): UsuarioBasicoResponseDto {
            errorObtenerPorFirebase?.let { throw it }
            return respuestaCrear
        }
    }
}
