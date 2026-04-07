package dam2.tfg.psicologiaapp.nota.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.nota.data.local.NotaDao
import dam2.tfg.psicologiaapp.nota.data.mappers.toEntity
import dam2.tfg.psicologiaapp.nota.data.mappers.toDomain
import dam2.tfg.psicologiaapp.nota.data.remote.NotaApi
import dam2.tfg.psicologiaapp.nota.data.remote.NotaRequestDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class NotaRepositoryImpl @Inject constructor(
    private val notaApi: NotaApi,
    private val proveedorTokenFirebase: ProveedorTokenFirebase,
    private val notaDao: NotaDao,
    private val preferenciasDataStore: DataStore<Preferences>,
) : NotaRepository {

    override suspend fun getNotasPacienteActual(): Result<List<Nota>> = runCatching {
        sincronizarNotasPacienteActualSiProcede()
        notaDao.listarTodas().map { it.toDomain() }
    }

    override suspend fun getNotasDePaciente(pacienteId: Long): Result<List<Nota>> = runCatching {
        sincronizarNotasDePacienteSiProcede(pacienteId)
        notaDao.listarPorPacienteId(pacienteId).map { it.toDomain() }
    }

    override fun observarNotasPacienteActual(): Flow<List<Nota>> =
        notaDao.observarTodas().map { lista -> lista.map { it.toDomain() } }

    override fun observarNotasDePaciente(pacienteId: Long): Flow<List<Nota>> =
        notaDao.observarPorPacienteId(pacienteId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun sincronizarNotasPacienteActual(): Result<Unit> = runCatching {
        sincronizarNotasPacienteActualSiProcede()
    }

    override suspend fun sincronizarNotasDePaciente(pacienteId: Long): Result<Unit> = runCatching {
        sincronizarNotasDePacienteSiProcede(pacienteId)
    }

    override suspend fun crearNota(
        asunto: String,
        descripcion: String
    ): Result<Nota> = runCatching {
        val creada = notaApi.crearNota(NotaRequestDto(asunto = asunto, descripcion = descripcion))
        notaDao.guardar(creada.toEntity())
        actualizarEstadoLocalPacienteActualDesdeRemoto()
        creada.toDomain()
    }

    override suspend fun actualizarNota(
        notaId: Long,
        asunto: String,
        descripcion: String
    ): Result<Nota> = runCatching {
        val actualizada = notaApi.actualizarNota(notaId, NotaRequestDto(asunto = asunto, descripcion = descripcion))
        notaDao.guardar(actualizada.toEntity())
        actualizarEstadoLocalPacienteActualDesdeRemoto()
        actualizada.toDomain()
    }

    override suspend fun borrarNota(notaId: Long): Result<Unit> = runCatching {
        notaApi.borrarNota(notaId)
        notaDao.borrarPorId(notaId)
        actualizarEstadoLocalPacienteActualDesdeRemoto()
    }

    private suspend fun sincronizarNotasPacienteActualSiProcede() {
        val respuestaEstado = ejecutarConRefrescoTokenSi401 {
            notaApi.getEstadoNotasPacienteActual()
        }
        if (!respuestaEstado.isSuccessful) {
            val codigo = respuestaEstado.code()
            val detalle = when (codigo) {
                401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                403 -> "HTTP 403: el usuario no tiene rol PACIENTE en la API (revisa la BD)"
                else -> "HTTP $codigo"
            }
            throw IllegalStateException("Error al obtener estado de notas: $detalle")
        }

        val estadoRemoto = respuestaEstado.body()
        val ultimaRemota = estadoRemoto?.ultimaModificacion
        val totalRemoto = estadoRemoto?.total ?: 0L

        val ultimaLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveUltimaPacienteActual) }
        val totalLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveTotalPacienteActual) } ?: -1L

        val sinCambios = (ultimaLocal == ultimaRemota) && (totalLocal == totalRemoto)
        if (sinCambios) return

        val respuestaLista = ejecutarConRefrescoTokenSi401 {
            notaApi.getNotasPacienteActual()
        }
        if (respuestaLista.code() == 204) {
            notaDao.borrarTodas()
            guardarEstadoPacienteActual(ultimaRemota, totalRemoto)
            return
        }
        if (!respuestaLista.isSuccessful) {
            val codigo = respuestaLista.code()
            val detalle = when (codigo) {
                401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                403 -> "HTTP 403: el usuario no tiene rol PACIENTE en la API (revisa la BD)"
                else -> "HTTP $codigo"
            }
            throw IllegalStateException("Error al obtener notas: $detalle")
        }

        val notas = respuestaLista.body().orEmpty()
        notaDao.borrarTodas()
        notaDao.guardarTodas(notas.map { it.toEntity() })
        guardarEstadoPacienteActual(ultimaRemota, totalRemoto)
    }

    private suspend fun sincronizarNotasDePacienteSiProcede(pacienteId: Long) {
        val respuestaEstado = ejecutarConRefrescoTokenSi401 {
            notaApi.getEstadoNotasDePaciente(pacienteId)
        }
        if (!respuestaEstado.isSuccessful) {
            val codigo = respuestaEstado.code()
            val detalle = when (codigo) {
                401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                403 -> "HTTP 403: no autorizado para ver notas de este paciente"
                else -> "HTTP $codigo"
            }
            throw IllegalStateException("Error al obtener estado de notas: $detalle")
        }

        val estadoRemoto = respuestaEstado.body()
        val ultimaRemota = estadoRemoto?.ultimaModificacion
        val totalRemoto = estadoRemoto?.total ?: 0L

        val claveUltima = claveUltimaPorPacienteId(pacienteId)
        val claveTotal = claveTotalPorPacienteId(pacienteId)

        val ultimaLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveUltima) }
        val totalLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveTotal) } ?: -1L

        val sinCambios = (ultimaLocal == ultimaRemota) && (totalLocal == totalRemoto)
        if (sinCambios) return

        val respuestaLista = ejecutarConRefrescoTokenSi401 {
            notaApi.getNotasDePaciente(pacienteId)
        }
        if (respuestaLista.code() == 204) {
            notaDao.borrarPorPacienteId(pacienteId)
            guardarEstadoPorPacienteId(pacienteId, ultimaRemota, totalRemoto)
            return
        }
        if (!respuestaLista.isSuccessful) {
            val codigo = respuestaLista.code()
            val detalle = when (codigo) {
                401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                403 -> "HTTP 403: no autorizado para ver notas de este paciente"
                else -> "HTTP $codigo"
            }
            throw IllegalStateException("Error al obtener notas: $detalle")
        }

        val notas = respuestaLista.body().orEmpty()
        notaDao.borrarPorPacienteId(pacienteId)
        notaDao.guardarTodas(notas.map { it.toEntity() })
        guardarEstadoPorPacienteId(pacienteId, ultimaRemota, totalRemoto)
    }

    private suspend fun actualizarEstadoLocalPacienteActualDesdeRemoto() {
        val respuestaEstado = ejecutarConRefrescoTokenSi401 {
            notaApi.getEstadoNotasPacienteActual()
        }
        if (!respuestaEstado.isSuccessful) return
        val estado = respuestaEstado.body()
        guardarEstadoPacienteActual(estado?.ultimaModificacion, estado?.total ?: 0L)
    }

    private suspend fun guardarEstadoPacienteActual(ultima: String?, total: Long) {
        preferenciasDataStore.edit { preferencias ->
            preferencias[claveUltimaPacienteActual] = ultima.orEmpty()
            preferencias[claveTotalPacienteActual] = total
        }
    }

    private suspend fun guardarEstadoPorPacienteId(pacienteId: Long, ultima: String?, total: Long) {
        val claveUltima = claveUltimaPorPacienteId(pacienteId)
        val claveTotal = claveTotalPorPacienteId(pacienteId)
        preferenciasDataStore.edit { preferencias ->
            preferencias[claveUltima] = ultima.orEmpty()
            preferencias[claveTotal] = total
        }
    }

    private fun claveUltimaPorPacienteId(pacienteId: Long) =
        stringPreferencesKey("sync_notas_paciente_${pacienteId}_ultima_modificacion")

    private fun claveTotalPorPacienteId(pacienteId: Long) =
        longPreferencesKey("sync_notas_paciente_${pacienteId}_total")

    private suspend fun <T> ejecutarConRefrescoTokenSi401(
        bloque: suspend () -> retrofit2.Response<T>
    ): retrofit2.Response<T> {
        var respuesta = bloque()
        if (respuesta.code() == 401) {
            proveedorTokenFirebase.obtenerToken(forzarRenovacion = true)
            respuesta = bloque()
        }
        return respuesta
    }

    private suspend fun <T> kotlinx.coroutines.flow.Flow<Preferences>.firstOrNullPreferencia(
        clave: Preferences.Key<T>
    ): T? = try {
        this.first()[clave]
    } catch (_: Throwable) {
        null
    }

    private companion object {
        val claveUltimaPacienteActual =
            stringPreferencesKey("sync_notas_paciente_actual_ultima_modificacion")
        val claveTotalPacienteActual =
            longPreferencesKey("sync_notas_paciente_actual_total")
    }
}
