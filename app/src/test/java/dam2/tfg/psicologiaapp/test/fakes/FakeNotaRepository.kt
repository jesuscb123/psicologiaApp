package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakeNotaRepository : NotaRepository {
    override suspend fun getNotasPacienteActual(): Result<List<Nota>> = Result.success(emptyList())
    override suspend fun getNotasDePaciente(pacienteId: Long): Result<List<Nota>> = Result.success(emptyList())
    override fun observarNotasPacienteActual(): Flow<List<Nota>> = emptyFlow()
    override fun observarNotasDePaciente(pacienteId: Long): Flow<List<Nota>> = emptyFlow()
    override suspend fun sincronizarNotasPacienteActual(): Result<Unit> = Result.success(Unit)
    override suspend fun sincronizarNotasDePaciente(pacienteId: Long): Result<Unit> = Result.success(Unit)
    override suspend fun crearNota(asunto: String, descripcion: String): Result<Nota> =
        Result.failure(NotImplementedError())
    override suspend fun actualizarNota(notaId: Long, asunto: String, descripcion: String): Result<Nota> =
        Result.failure(NotImplementedError())
    override suspend fun borrarNota(notaId: Long): Result<Unit> = Result.success(Unit)
}
