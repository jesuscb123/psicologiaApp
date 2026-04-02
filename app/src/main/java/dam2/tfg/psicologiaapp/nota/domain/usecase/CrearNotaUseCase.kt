package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject

class CrearNotaUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    suspend operator fun invoke(
        asunto: String,
        descripcion: String
    ): Result<Nota> = notaRepository.crearNota(asunto, descripcion)
}
