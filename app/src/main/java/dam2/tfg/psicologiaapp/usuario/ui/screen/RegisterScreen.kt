package dam2.tfg.psicologiaapp.usuario.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import dam2.tfg.psicologiaapp.core.utils.images.ImageUtils
import dam2.tfg.psicologiaapp.usuario.ui.viewmodel.UsuarioViewModel

@Composable
fun RegisterScreen(
    role: String,
    viewModel: UsuarioViewModel,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    // Estados del formulario
    var nombreUsuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var numeroColegiado by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }

    // Estados de la imagen
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }

    // Lanzador de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { base64Image = ImageUtils.uriToBase64(context, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de ${role.uppercase()}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        // 1. Selector de Foto
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri == null) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
            } else {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text("Foto de perfil", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Campo Nombre
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // 4. Campo Colegiado (Solo si es psicólogo)
        if (role == "psicologo") {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = numeroColegiado,
                onValueChange = { numeroColegiado = it },
                label = { Text("Número de Colegiado") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: M-12345") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = especialidad,
            onValueChange = { especialidad = it },
            label = { Text("Especialidad (ej. Clínica, Cognitiva)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña (mín. 6 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (uiState) {
            is UsuarioViewModel.UsuarioUiState.Loading -> CircularProgressIndicator()
            else -> {
                Button(
                    onClick = {
                        viewModel.registrarUsuario(
                            email = email,
                            password = password,
                            nombreUsuario = nombreUsuario,
                            fotoPerfilBase64 = base64Image,
                            numeroColegiado = if (role == "psicologo") numeroColegiado else null,
                            especialidad = especialidad
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    // Validación básica para habilitar botón
                    enabled = nombreUsuario.isNotBlank() && email.contains("@") && password.length >= 6 &&
                            (role != "psicologo" || numeroColegiado.isNotBlank())
                ) {
                    Text("Finalizar Registro")
                }
            }
        }

        // 7. Feedback de Errores
        if (uiState is UsuarioViewModel.UsuarioUiState.Error) {
            Text(
                text = (uiState as UsuarioViewModel.UsuarioUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Navegación al éxito
        LaunchedEffect(uiState) {
            if (uiState is UsuarioViewModel.UsuarioUiState.Success) {
                onNavigateToHome()
            }
        }
    }
}