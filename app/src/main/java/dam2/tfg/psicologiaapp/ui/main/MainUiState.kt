package dam2.tfg.psicologiaapp.ui.main

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val usuario: Usuario, val usuariosRegistrados: List<Usuario>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}