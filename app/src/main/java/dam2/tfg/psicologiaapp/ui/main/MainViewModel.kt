package dam2.tfg.psicologiaapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetUsuarioByFirebaseUidUsedCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetUsuariosRegistrados
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUsuarioByFirebaseUid: GetUsuarioByFirebaseUidUsedCase,
    private val getUsuariosRegistrados: GetUsuariosRegistrados
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
                // DEBUG: separar llamadas
                val usuario = try {
                    getUsuarioByFirebaseUid(currentUser.uid)
                } catch (e: Exception) {
                    _uiState.value = MainUiState.Error("Fallo en getUsuarioByFirebaseUid: ${e.message}")
                    return@launch
                }

                val usuariosRegistrados = try {
                    getUsuariosRegistrados()
                } catch (e: Exception) {
                    _uiState.value = MainUiState.Error("Fallo en getUsuariosRegistrados: ${e.message}")
                    return@launch
                }

                _uiState.value = MainUiState.Success(
                    usuario = usuario,
                    usuariosRegistrados = usuariosRegistrados
                )

            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}