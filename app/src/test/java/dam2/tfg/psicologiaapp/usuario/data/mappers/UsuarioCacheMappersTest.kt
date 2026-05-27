package dam2.tfg.psicologiaapp.usuario.data.mappers

import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioEntity
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UsuarioCacheMappersTest {

    private val pacienteEntity = UsuarioEntity(
        usuarioId = 1L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = null,
        rol = "PACIENTE",
        email = "ana@test.com",
        psicologoId = 10L,
    )

    @Test
    fun `UsuarioEntity toPerfilCacheado mapea rol y psicologoId`() {
        assertEquals(
            PerfilCacheado(
                usuarioId = 1L,
                firebaseUid = "uid-pac",
                nombre = "Ana",
                apellidos = "López",
                fotoPerfilUrl = null,
                rol = RolUsuario.PACIENTE,
                psicologoId = 10L,
            ),
            pacienteEntity.toPerfilCacheado(),
        )
    }

    @Test
    fun `PacientePerfil toEntityCache round-trip con toPerfilCacheado`() {
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

        val entity = perfil.toEntityCache()
        assertEquals(pacienteEntity, entity)
        assertEquals(pacienteEntity.toPerfilCacheado(), entity.toPerfilCacheado())
    }

    @Test
    fun `PsicologoPerfil toEntityCache no incluye psicologoId`() {
        val perfil = PsicologoPerfil(
            usuarioId = 2L,
            firebaseUid = "uid-psi",
            nombre = "Carlos",
            apellidos = "Ruiz",
            email = "carlos@test.com",
            fotoPerfilUrl = null,
            rol = RolUsuario.PSICOLOGO,
            numeroColegiado = "12345",
            especialidades = listOf("Clínica"),
        )

        val entity = perfil.toEntityCache()

        assertEquals(
            UsuarioEntity(
                usuarioId = 2L,
                firebaseUid = "uid-psi",
                nombre = "Carlos",
                apellidos = "Ruiz",
                fotoPerfilUrl = null,
                rol = "PSICOLOGO",
                email = "carlos@test.com",
                psicologoId = null,
            ),
            entity,
        )
    }

    @Test
    fun `UsuarioEntity toPacientePerfil devuelve perfil solo si rol es PACIENTE`() {
        assertEquals(
            PacientePerfil(
                usuarioId = 1L,
                firebaseUid = "uid-pac",
                nombre = "Ana",
                apellidos = "López",
                email = "ana@test.com",
                fotoPerfilUrl = null,
                rol = RolUsuario.PACIENTE,
                psicologoId = 10L,
            ),
            pacienteEntity.toPacientePerfil(),
        )

        val psicologoEntity = pacienteEntity.copy(rol = "PSICOLOGO", psicologoId = null)
        assertNull(psicologoEntity.toPacientePerfil())
    }

    @Test
    fun `UsuarioEntity toPsicologoPerfil devuelve perfil minimo si rol es PSICOLOGO`() {
        val entity = UsuarioEntity(
            usuarioId = 2L,
            firebaseUid = "uid-psi",
            nombre = "Carlos",
            apellidos = "Ruiz",
            fotoPerfilUrl = null,
            rol = "PSICOLOGO",
            email = "carlos@test.com",
        )

        assertEquals(
            PsicologoPerfil(
                usuarioId = 2L,
                firebaseUid = "uid-psi",
                nombre = "Carlos",
                apellidos = "Ruiz",
                email = "carlos@test.com",
                fotoPerfilUrl = null,
                rol = RolUsuario.PSICOLOGO,
                numeroColegiado = "",
                especialidades = emptyList(),
            ),
            entity.toPsicologoPerfil(),
        )

        assertNull(pacienteEntity.toPsicologoPerfil())
    }

    @Test
    fun `UsuarioPerfilBasico toEntityCache mapea rol SIN_ROL`() {
        val perfil = UsuarioPerfilBasico(
            usuarioId = 3L,
            firebaseUid = "uid",
            nombre = "Basico",
            apellidos = "User",
            email = "b@test.com",
            fotoPerfilUrl = null,
            rol = RolUsuario.SIN_ROL,
        )

        assertEquals("SIN_ROL", perfil.toEntityCache().rol)
    }
}
