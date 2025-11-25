package dam2.tfg.psicologiaapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
                    Text("Usuarios registrados:")
                    s.usuariosRegistrados.forEach {
                        Card(modifier = Modifier.fillMaxWidth()){
                            Text(it.nombreUsuario)
                            Text(it.email)
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                }
            }

            is MainUiState.Error -> {
                Text("Error: ${s.message}")
            }
        }
    }
}