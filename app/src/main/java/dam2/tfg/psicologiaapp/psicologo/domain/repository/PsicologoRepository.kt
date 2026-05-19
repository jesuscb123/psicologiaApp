package dam2.tfg.psicologiaapp.psicologo.domain.repository

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo

import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de psicólogos en dominio.
 */
interface PsicologoRepository {

    suspend fun listarPsicologos(): Result<List<Psicologo>>

    suspend fun buscarPsicologos(nombreUsuario: String): Result<List<Psicologo>>

    suspend fun getPsicologoPorFirebase(firebaseId: String): Result<Psicologo>

    suspend fun getPacientesDePsicologo(): Result<List<Paciente>>

    suspend fun actualizarMiDescripcion(descripcion: String?): Result<Psicologo>

    suspend fun actualizarMisEspecialidades(especialidades: List<String>): Result<Psicologo>

    fun observarPsicologos(): Flow<List<Psicologo>>

    fun observarPacientesDePsicologo(): Flow<List<Paciente>>
}
