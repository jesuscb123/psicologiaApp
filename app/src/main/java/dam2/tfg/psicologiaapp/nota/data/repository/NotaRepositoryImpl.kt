package dam2.tfg.psicologiaapp.nota.data.repository

import dam2.tfg.psicologiaapp.nota.data.mappers.toDomain
import dam2.tfg.psicologiaapp.nota.data.remote.NotaApi
import dam2.tfg.psicologiaapp.nota.data.remote.NotaRequestDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotaRepositoryImpl @Inject constructor(
    private val notaApi: NotaApi
) : NotaRepository {

    override suspend fun getNotasPacienteActual(): Result<List<Nota>> = runCatching {
        val respuesta: Response<List<dam2.tfg.psicologiaapp.nota.data.remote.NotaResponseDto>> =
            notaApi.getNotasPacienteActual()

        if (respuesta.code() == 204) {
            emptyList()
        } else {
            if (!respuesta.isSuccessful) {
                throw IllegalStateException("Error al obtener notas: HTTP ${respuesta.code()}")
            }
            respuesta.body()?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun getNotasDePaciente(pacienteId: Long): Result<List<Nota>> = runCatching {
        notaApi.getNotasDePaciente(pacienteId).map { it.toDomain() }
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
