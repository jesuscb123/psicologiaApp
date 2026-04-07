package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.GetPacientesDePsicologoUseCase
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
class HomePsicologoViewModel @Inject constructor(
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val getPacientesDePsicologoUseCase: GetPacientesDePsicologoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePsicologoUiState())
    val uiState: StateFlow<HomePsicologoUiState> = _uiState

    private var trabajoRecarga: Job? = null

    fun recargar() {
        trabajoRecarga?.cancel()
        trabajoRecarga = viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoPerfil = getPerfilActualUseCase()
            ensureActive()

            resultadoPerfil.fold(
                onSuccess = { perfil ->
                    if (perfil.rol != RolUsuario.PSICOLOGO) {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = "Perfil no válido para psicólogo"
                            )
                        }
                        return@launch
                    }
                    val perfilPsi = perfil as? PsicologoPerfil
                    if (perfilPsi == null) {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                mensajeError = "No se pudo cargar el perfil de psicólogo"
                            )
                        }
                        return@launch
                    }

                    _uiState.update {
                        it.copy(nombreUsuarioPsicologo = perfilPsi.nombreUsuario)
                    }

                    val resultadoPacientes = getPacientesDePsicologoUseCase()
                    ensureActive()
                    resultadoPacientes.fold(
                        onSuccess = { pacientes ->
                            _uiState.update { estado ->
                                estado.copy(
                                    cargando = false,
                                    listaPacientes = pacientes,
                                    mensajeError = null,
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    cargando = false,
                                    mensajeError = error.message
                                        ?: "No se pudieron cargar los pacientes",
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el perfil"
                        )
                    }
                }
            )
        }
    }
}
