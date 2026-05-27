package dam2.tfg.psicologiaapp.di

import com.google.gson.GsonBuilder
import dam2.tfg.psicologiaapp.paciente.data.remote.PacientePerfilResponseDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoPerfilResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GsonAdaptadoresTest {

    private val gson = GsonBuilder()
        .registerTypeAdapter(UsuarioResponseDto::class.java, UsuarioResponseDtoDeserializer)
        .registerTypeAdapter(UsuarioPerfilResponseDto::class.java, UsuarioPerfilResponseDtoDeserializer)
        .registerTypeAdapter(UsuarioRequestDto::class.java, UsuarioRequestDtoSerializer)
        .create()

    @Test
    fun `UsuarioResponseDtoDeserializer deserializa psicologo por rol`() {
        val json = """
            {
              "id": 1,
              "firebaseUid": "uid-psi",
              "nombre": "Carlos",
              "apellidos": "Ruiz",
              "fotoPerfilUrl": null,
              "rol": "PSICOLOGO",
              "idEntidadPsicologo": 10,
              "numeroColegiado": "12345",
              "especialidades": ["Clínica"],
              "descripcion": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, UsuarioResponseDto::class.java)

        assertTrue(dto is PsicologoResponseDto)
        assertEquals(10L, (dto as PsicologoResponseDto).idEntidadPsicologo)
    }

    @Test
    fun `UsuarioResponseDtoDeserializer deserializa paciente por rol`() {
        val json = """
            {
              "id": 2,
              "firebaseUid": "uid-pac",
              "nombre": "Ana",
              "apellidos": "López",
              "fotoPerfilUrl": null,
              "rol": "PACIENTE",
              "psicologoId": 10,
              "idPaciente": 100
            }
        """.trimIndent()

        val dto = gson.fromJson(json, UsuarioResponseDto::class.java)

        assertTrue(dto is PacienteResponseDto)
        assertEquals(100L, (dto as PacienteResponseDto).idPaciente)
    }

    @Test
    fun `UsuarioResponseDtoDeserializer deserializa usuario basico si rol desconocido`() {
        val json = """
            {
              "id": 3,
              "firebaseUid": "uid",
              "nombre": "Sin",
              "apellidos": "Rol",
              "fotoPerfilUrl": null,
              "rol": "SIN_ROL"
            }
        """.trimIndent()

        val dto = gson.fromJson(json, UsuarioResponseDto::class.java)

        assertTrue(dto is UsuarioBasicoResponseDto)
    }

    @Test
    fun `UsuarioPerfilResponseDtoDeserializer deserializa perfil psicologo`() {
        val json = """
            {
              "id": 1,
              "firebaseUid": "uid-psi",
              "nombre": "Carlos",
              "apellidos": "Ruiz",
              "email": "carlos@test.com",
              "fotoPerfilUrl": null,
              "rol": "PSICOLOGO",
              "numeroColegiado": "12345",
              "especialidades": ["Clínica"],
              "descripcion": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, UsuarioPerfilResponseDto::class.java)

        assertTrue(dto is PsicologoPerfilResponseDto)
    }

    @Test
    fun `UsuarioPerfilResponseDtoDeserializer deserializa perfil paciente`() {
        val json = """
            {
              "id": 2,
              "firebaseUid": "uid-pac",
              "nombre": "Ana",
              "apellidos": "López",
              "email": "ana@test.com",
              "fotoPerfilUrl": null,
              "rol": "PACIENTE",
              "psicologoId": 10
            }
        """.trimIndent()

        val dto = gson.fromJson(json, UsuarioPerfilResponseDto::class.java)

        assertTrue(dto is PacientePerfilResponseDto)
    }

    @Test
    fun `UsuarioPerfilResponseDtoDeserializer deserializa perfil basico si rol desconocido`() {
        val json = """
            {
              "id": 3,
              "firebaseUid": "uid",
              "nombre": "Basico",
              "apellidos": "User",
              "email": "b@test.com",
              "fotoPerfilUrl": null,
              "rol": "OTRO"
            }
        """.trimIndent()

        val dto = gson.fromJson(json, UsuarioPerfilResponseDto::class.java)

        assertTrue(dto is UsuarioPerfilBasicoResponseDto)
    }

    @Test
    fun `UsuarioRequestDtoSerializer serializa PsicologoRequestDto con campos concretos`() {
        val dto: UsuarioRequestDto = PsicologoRequestDto(
            nombre = "Carlos",
            apellidos = "Ruiz",
            fotoPerfilUrl = null,
            numeroColegiado = "12345",
            especialidades = listOf("Clínica"),
            descripcion = null,
        )

        val json = gson.toJson(dto)
        val parsed = gson.fromJson(json, PsicologoRequestDto::class.java)

        assertEquals("12345", parsed.numeroColegiado)
        assertEquals(listOf("Clínica"), parsed.especialidades)
    }

    @Test
    fun `UsuarioRequestDtoSerializer serializa PacienteRequestDto con psicologoId`() {
        val dto: UsuarioRequestDto = PacienteRequestDto(
            nombre = "Ana",
            apellidos = "López",
            fotoPerfilUrl = null,
            psicologoId = 10L,
        )

        val json = gson.toJson(dto)
        val parsed = gson.fromJson(json, PacienteRequestDto::class.java)

        assertEquals(10L, parsed.psicologoId)
    }
}
