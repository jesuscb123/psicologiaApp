package dam2.tfg.psicologiaapp.ui.Registro

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dam2.tfg.psicologiaapp.ui.screens.auth.RegistroUiState
import dam2.tfg.psicologiaapp.ui.screens.auth.RegistroViewModel

@Composable
fun RegistroPantalla(
    navController: NavController,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var nombreUsuario by rememberSaveable { mutableStateOf("") }

    // Reaccionar a cambios de estado (éxito / error)
    LaunchedEffect(state) {
        when (state) {
            is RegistroUiState.Success -> {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                navController.popBackStack()
            }
            is RegistroUiState.Error -> {
                val message = (state as RegistroUiState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nombre de usuario")
        TextField(nombreUsuario, onValueChange = { nombreUsuario = it })

        Text("Introduce tu email")
        TextField(email, onValueChange = { email = it })

        Text("Introduce tu contraseña")
        TextField(password, onValueChange = { password = it })

        when (state) {
            is RegistroUiState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                Button(onClick = {
                    viewModel.registrar(
                        email = email,
                        password = password,
                        nombreUsuario = nombreUsuario.ifBlank {
                            email.substringBefore("@")
                        }
                    )
                }) {
                    Text("Registrarse")
                }
            }
        }
    }
}