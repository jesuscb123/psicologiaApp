package dam2.tfg.psicologiaapp.psicologo.domain.repository

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

/**
 * Contrato del repositorio de psicólogos en dominio.
 */
interface PsicologoRepository {

    suspend fun listarPsicologos(): Result<List<Psicologo>>

    suspend fun buscarPsicologos(nombreUsuario: String): Result<List<Psicologo>>

    suspend fun getPsicologoPorFirebase(firebaseId: String): Result<Psicologo>

    suspend fun getPacientesDePsicologo(): Result<List<Paciente>>
}
