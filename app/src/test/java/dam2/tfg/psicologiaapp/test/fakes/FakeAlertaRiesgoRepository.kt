package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.AlertaRiesgoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakeAlertaRiesgoRepository : AlertaRiesgoRepository {
    override fun observarAlertasRiesgo(psicologoUid: String): Flow<Set<Long>> = emptyFlow()
    override suspend fun marcarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {}
    override suspend fun limpiarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {}
}
