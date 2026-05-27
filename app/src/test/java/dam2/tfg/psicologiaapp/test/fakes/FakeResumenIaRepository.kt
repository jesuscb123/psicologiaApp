package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa
import dam2.tfg.psicologiaapp.resumenIa.domain.repository.ResumenIaRepository

open class FakeResumenIaRepository : ResumenIaRepository {
    override suspend fun generarResumenNotasPaciente(pacienteId: Long): Result<ResumenIa> =
        Result.failure(NotImplementedError())
}
