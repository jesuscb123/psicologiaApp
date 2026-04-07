package dam2.tfg.psicologiaapp.usuario.domain.model

/**
 * Modelos de dominio genéricos para usuario,
 * alineados con el backend pero desacoplados de la capa de red.
 *
 * Los modelos específicos de paciente y psicólogo viven
 * en sus agregados respectivos (`paciente.domain.model` y `psicologo.domain.model`).
 */

interface Usuario {
    val usuarioId: Long
    val firebaseUid: String
    val nombre: String
    val apellidos: String
    val fotoPerfilUrl: String?
    val rol: RolUsuario
}

/**
 * Modelo de dominio genérico para creación de usuario (alineado con UsuarioRequestDto del backend).
 *
 * Las implementaciones concretas (por ejemplo, `PsicologoRequest` y `PacienteRequest`)
 * residen en los paquetes de dominio de sus agregados.
 */
interface UsuarioRequest {
    val nombre: String
    val apellidos: String
    val fotoPerfilUrl: String?
}

fun Usuario.nombreCompleto(): String =
    listOf(nombre, apellidos).filter { it.isNotBlank() }.joinToString(" ")

