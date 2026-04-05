package dam2.tfg.psicologiaapp.presentation.ui.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.nota.domain.usecase.BorrarNotaUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.GetNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ListarPsicologosUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePacienteViewModel @Inject constructor(
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val listarPsicologosUseCase: ListarPsicologosUseCase,
    private val getNotasPacienteActualUseCase: GetNotasPacienteActualUseCase,
    private val borrarNotaUseCase: BorrarNotaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePacienteUiState())
    val uiState: StateFlow<HomePacienteUiState> = _uiState

    private var trabajoRecarga: Job? = null

    fun recargar() {
        trabajoRecarga?.cancel()
        trabajoRecarga = viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoPerfil = getPerfilActualUseCase()
            ensureActive()
            val resultadoPsicologos = listarPsicologosUseCase()
            ensureActive()

            var perfilPaciente: PacientePerfil? = null
            resultadoPerfil.fold(
                onSuccess = { perfil ->
                    if (perfil.rol != RolUsuario.PACIENTE) {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = "Perfil no válido para paciente"
                            )
                        }
                        return@launch
                    }
                    perfilPaciente = perfil as? PacientePerfil
                    if (perfilPaciente == null) {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = "No se pudo cargar el perfil de paciente"
                            )
                        }
                        return@launch
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el perfil"
                        )
                    }
                    return@launch
                }
            )

            val listaPsicologos = resultadoPsicologos.getOrElse { emptyList() }
            val psicologoAsignado = obtenerPsicologoAsignado(
                listaPsicologos = listaPsicologos,
                psicologoId = perfilPaciente?.psicologoId
            )

            _uiState.update {
                it.copy(
                    perfilPaciente = perfilPaciente,
                    listaPsicologos = listaPsicologos,
                    psicologoAsignado = psicologoAsignado,
                    mensajeError = resultadoPsicologos.exceptionOrNull()?.message,
                )
            }

            if (perfilPaciente?.psicologoId != null) {
                val resultadoNotas = getNotasPacienteActualUseCase()
                ensureActive()
                resultadoNotas.fold(
                    onSuccess = { notas ->
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                notas = notas,
                                mensajeError = it.mensajeError
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = error.message ?: "No se pudieron cargar las notas"
                            )
                        }
                    }
                )
            } else {
                _uiState.update { it.copy(cargando = false, notas = emptyList()) }
            }
        }
    }

    fun eliminarNota(notaId: Long) {
        viewModelScope.launch {
            borrarNotaUseCase(notaId).fold(
                onSuccess = {
                    _uiState.update { estado ->
                        estado.copy(
                            notas = estado.notas.filter { nota -> nota.id != notaId },
                            mensajeError = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mensajeError = error.message ?: "No se pudo eliminar la nota"
                        )
                    }
                }
            )
        }
    }

    private fun obtenerPsicologoAsignado(
        listaPsicologos: List<Psicologo>,
        psicologoId: Long?,
    ): Psicologo? {
        if (psicologoId == null) return null
        return listaPsicologos.firstOrNull { it.idEntidadPsicologo == psicologoId }
    }
}

