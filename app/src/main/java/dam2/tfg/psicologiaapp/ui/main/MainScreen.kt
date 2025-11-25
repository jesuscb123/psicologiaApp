package dam2.tfg.psicologiaapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarUsuario()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val s = state) {
            is MainUiState.Loading -> CircularProgressIndicator()

            is MainUiState.Success -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hola, ${s.usuario.nombreUsuario}")
                    Spacer(Modifier.height(8.dp))
                    Text("Email: ${s.usuario.email}")
                }
            }

            is MainUiState.Error -> {
                Text("Error: ${s.message}")
            }
        }
    }
}