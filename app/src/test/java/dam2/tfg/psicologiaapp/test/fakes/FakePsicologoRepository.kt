package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakePsicologoRepository : PsicologoRepository {
    override suspend fun listarPsicologos(): Result<List<Psicologo>> = Result.success(emptyList())
    override suspend fun buscarPsicologos(nombreUsuario: String): Result<List<Psicologo>> = Result.success(emptyList())
    override suspend fun getPsicologoPorFirebase(firebaseId: String): Result<Psicologo> = Result.failure(NotImplementedError())
    override suspend fun getPacientesDePsicologo(): Result<List<Paciente>> = Result.success(emptyList())
    override suspend fun actualizarMiDescripcion(descripcion: String?): Result<Psicologo> = Result.failure(NotImplementedError())
    override suspend fun actualizarMisEspecialidades(especialidades: List<String>): Result<Psicologo> = Result.failure(NotImplementedError())
    override fun observarPsicologos(): Flow<List<Psicologo>> = emptyFlow()
    override fun observarPacientesDePsicologo(): Flow<List<Paciente>> = emptyFlow()
}
