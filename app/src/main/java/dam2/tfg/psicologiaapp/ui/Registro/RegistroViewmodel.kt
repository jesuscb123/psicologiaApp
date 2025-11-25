package dam2.tfg.psicologiaapp.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam2.tfg.psicologiaapp.usuario.domain.usecase.RegistrarUsuarioEnBackendUseCase
import dam2.tfg.psicologiaapp.utils.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val registrarUsuarioEnBackend: RegistrarUsuarioEnBackendUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Idle)
    val uiState: StateFlow<RegistroUiState> = _uiState

    fun registrar(email: String, password: String, nombreUsuario: String) {
        viewModelScope.launch {
            _uiState.value = RegistroUiState.Loading

            try {
                val registradoEnFirebase = authManager.crearUsuarioConEmailPassword(email, password)

                if (!registradoEnFirebase) {
                    _uiState.value = RegistroUiState.Error("Error al registrar en Firebase")
                    return@launch
                }

                val firebaseUser = FirebaseAuth.getInstance().currentUser
                    ?: throw IllegalStateException("UID de Firebase nulo tras el registro")

                val tokenResultado = firebaseUser.getIdToken(true).await()
                val idToken = tokenResultado?.token
                    ?: throw IllegalStateException("Token de Firebase nulo")

                val usuario = registrarUsuarioEnBackend(
                    idToken = idToken,
                    nombreUsuario = nombreUsuario.ifBlank { email.substringBefore("@") },
                )

                _uiState.value = RegistroUiState.Success(usuario)

            } catch (e: Exception) {
                _uiState.value = RegistroUiState.Error(
                    e.message ?: "Error desconocido en el registro"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegistroUiState.Idle
    }
}
