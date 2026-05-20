package dam2.tfg.psicologiaapp.presentation.ui.paciente.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.EstablecerModoTemaUseCase
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AjustesPacienteViewModel @Inject constructor(
    private val observarModoTemaUseCase: ObservarModoTemaUseCase,
    private val establecerModoTemaUseCase: EstablecerModoTemaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesPacienteUiState())
    val uiState: StateFlow<AjustesPacienteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observarModoTemaUseCase().collect { modo ->
                _uiState.update { it.copy(modoTema = modo) }
            }
        }
    }

    fun fijarModoTema(modo: ModoTemaApp) {
        viewModelScope.launch {
            establecerModoTemaUseCase(modo)
        }
    }
}
