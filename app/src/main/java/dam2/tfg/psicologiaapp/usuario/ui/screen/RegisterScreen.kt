package dam2.tfg.psicologiaapp.usuario.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.usuario.ui.viewmodel.UsuarioViewModel

@Composable
fun RegisterScreen(
    role: String,
    viewModel: UsuarioViewModel,
    onNavigateToHome: () -> Unit
) {
    // 1. Estados para los campos
    var nombreUsuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 2. Estado nuevo para el psicólogo
    var numeroColegiado by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    // Añadimos scroll por si el formulario crece
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState), // <--- Muy importante para UX
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registro de ${role.uppercase()}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- 3. Lógica Condicional Senior ---
        if (role == "psicologo") {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = numeroColegiado,
                onValueChange = { numeroColegiado = it },
                label = { Text("Número de Colegiado") },
                placeholder = { Text("Ej: M-12345") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (uiState) {
            is UsuarioViewModel.UsuarioUiState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                Button(
                    onClick = {
                        // 4. Pasamos el dato al ViewModel
                        viewModel.registrarUsuario(
                            firebaseUid = "temp_uid_${(1..100).random()}",
                            email = email,
                            nombreUsuario = nombreUsuario,
                            fotoPerfilBase64 = null,
                            numeroColegiado = if (role == "psicologo") numeroColegiado else null
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    // Opcional: Desactivar botón si es psicólogo y no hay número
                    enabled = (role != "psicologo" || numeroColegiado.isNotBlank())
                ) {
                    Text("Finalizar Registro")
                }
            }
        }

        // Manejo de éxito
        LaunchedEffect(uiState) {
            if (uiState is UsuarioViewModel.UsuarioUiState.Success) {
                onNavigateToHome()
            }
        }

        // Manejo de error
        if (uiState is UsuarioViewModel.UsuarioUiState.Error) {
            Text(
                text = (uiState as UsuarioViewModel.UsuarioUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}