package dam2.tfg.psicologiaapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ModoTemaActividadViewModel @Inject constructor(
    observarModoTemaUseCase: ObservarModoTemaUseCase,
) : ViewModel() {

    val modoTema: StateFlow<ModoTemaApp> = observarModoTemaUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ModoTemaApp.SeguirSistema,
        )
}
