package dam2.tfg.psicologiaapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam2.tfg.psicologiaapp.domain.model.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import dam2.tfg.psicologiaapp.domain.usecase.GetUsuarioByFirebaseUidUsedCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val usuario: Usuario) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUsuarioByFirebaseUid: GetUsuarioByFirebaseUidUsedCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    fun cargarUsuario() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _uiState.value = MainUiState.Error("No hay usuario autenticado en Firebase")
            return
        }

        viewModelScope.launch {
            try {
                val usuario = getUsuarioByFirebaseUid(currentUser.uid)
                _uiState.value = MainUiState.Success(usuario)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}