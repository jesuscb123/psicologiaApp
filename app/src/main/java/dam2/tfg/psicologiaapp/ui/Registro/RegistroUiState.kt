package dam2.tfg.psicologiaapp.ui.screens.auth

import dam2.tfg.psicologiaapp.domain.model.Usuario

sealed interface RegistroUiState {
    object Idle : RegistroUiState
    object Loading : RegistroUiState
    data class Success(val usuario: Usuario) : RegistroUiState
    data class Error(val message: String) : RegistroUiState
}