package dam2.tfg.psicologiaapp.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dam2.tfg.psicologiaapp.utils.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun IniciarSesionPantalla(navController: NavController, auth: AuthManager){
    val context = LocalContext.current
    var scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Iniciar Sesión")
        Spacer(Modifier.padding(8.dp))
        Text("Email")
        TextField(email, onValueChange = { email = it })
        Text("Contraseña")
        TextField(password, onValueChange = { password = it })
        Button(onClick = {
            scope.launch {
                iniciarSesion(email, password, auth, navController,context)
            }
        }) { }
        HorizontalDivider()
        Button(onClick = {
            navController.navigate("registro")
        }) { Text("Registro") }
    }
}

suspend fun iniciarSesion(email: String, password: String, auth: AuthManager, navController: NavController, context: Context){
    if (auth.iniciarSesionConEmailPassword(email, password)){
        Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
        navController.navigate("iniciarSesion")
    }
}

