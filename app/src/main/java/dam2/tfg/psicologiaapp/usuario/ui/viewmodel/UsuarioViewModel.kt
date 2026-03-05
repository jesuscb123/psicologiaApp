package dam2.tfg.psicologiaapp.usuario.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.RegistrarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase
) : ViewModel() {

    sealed class UsuarioUiState {
        object Initial : UsuarioUiState()
        object Loading : UsuarioUiState()
        data class Success(val usuario: Usuario) : UsuarioUiState()
        data class Error(val message: String) : UsuarioUiState()
    }

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Initial)
    val uiState: StateFlow<UsuarioUiState> = _uiState

    fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        fotoPerfilBase64: String?,
        numeroColegiado: String? = null,
        especialidad: String? = null,
    ) {
        _uiState.value = UsuarioUiState.Loading

        viewModelScope.launch {
            val result = registrarUsuarioUseCase(
                email = email,
                password = password,
                nombreUsuario = nombreUsuario,
                fotoPerfilBase64 = fotoPerfilBase64,
                numeroColegiado = numeroColegiado,
                especialidad = especialidad
            )

            result.fold(
                onSuccess = { usuarioGuardado ->
                    _uiState.value = UsuarioUiState.Success(usuarioGuardado)
                },
                onFailure = { excepcion ->
                    _uiState.value = UsuarioUiState.Error(excepcion.message ?: "Error al registrar")
                }
            )
        }
    }
}