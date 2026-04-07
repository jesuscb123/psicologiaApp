package dam2.tfg.psicologiaapp.nota.data.repository

import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.nota.data.mappers.toDomain
import dam2.tfg.psicologiaapp.nota.data.remote.NotaApi
import dam2.tfg.psicologiaapp.nota.data.remote.NotaRequestDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotaRepositoryImpl @Inject constructor(
    private val notaApi: NotaApi,
    private val proveedorTokenFirebase: ProveedorTokenFirebase,
) : NotaRepository {

    override suspend fun getNotasPacienteActual(): Result<List<Nota>> = runCatching {
        var respuesta = notaApi.getNotasPacienteActual()
        if (respuesta.code() == 401) {
            proveedorTokenFirebase.obtenerToken(forzarRenovacion = true)
            respuesta = notaApi.getNotasPacienteActual()
        }
        if (respuesta.code() == 204) {
            emptyList()
        } else {
            if (!respuesta.isSuccessful) {
                val codigo = respuesta.code()
                val detalle = when (codigo) {
                    401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                    403 -> "HTTP 403: el usuario no tiene rol PACIENTE en la API (revisa la BD)"
                    else -> "HTTP $codigo"
                }
                throw IllegalStateException("Error al obtener notas: $detalle")
            }
            respuesta.body()?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun getNotasDePaciente(pacienteId: Long): Result<List<Nota>> = runCatching {
        val respuesta = notaApi.getNotasDePaciente(pacienteId)
        if (!respuesta.isSuccessful) {
            val codigo = respuesta.code()
            val detalle = when (codigo) {
                401 -> "HTTP 401: el servidor no aceptó el token (revisa logs del API y FIREBASE_CREDENTIALS / proyecto)"
                403 -> "HTTP 403: no autorizado para ver notas de este paciente"
                else -> "HTTP $codigo"
            }
            throw IllegalStateException("Error al obtener notas: $detalle")
        }
        if (respuesta.code() == 204 || respuesta.body() == null) {
            emptyList()
        } else {
            respuesta.body()!!.map { it.toDomain() }
        }
    }

    override suspend fun crearNota(
        asunto: String,
        descripcion: String
    ): Result<Nota> = runCatching {
        notaApi.crearNota(NotaRequestDto(asunto = asunto, descripcion = descripcion)).toDomain()
    }

    override suspend fun actualizarNota(
        notaId: Long,
        asunto: String,
        descripcion: String
    ): Result<Nota> = runCatching {
        notaApi.actualizarNota(notaId, NotaRequestDto(asunto = asunto, descripcion = descripcion)).toDomain()
    }

    override suspend fun borrarNota(notaId: Long): Result<Unit> = runCatching {
        notaApi.borrarNota(notaId)
    }
}
