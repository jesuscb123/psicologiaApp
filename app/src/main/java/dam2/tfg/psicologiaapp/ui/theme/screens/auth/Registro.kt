package dam2.tfg.psicologiaapp.ui.theme.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import androidx.navigation.NavController
import dam2.tfg.psicologiaapp.utils.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun RegistroPantalla(navController: NavController, auth: AuthManager){
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Introduce tu email")
        TextField(email, onValueChange = {email = it})
        Text("Introduce tu contraseña")
        TextField(password, onValueChange = {password = it})
        Button(onClick = {
            scope.launch {
                registro(email, password, auth, navController, context)
            }
        }){
            Text("Registrarse")
        }
    }
}

private suspend fun registro(email: String, password: String, auth: AuthManager, navController: NavController, context: Context){
    if (auth.crearUsuarioConEmailPassword(email, password)) {
        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
        navController.popBackStack()
    }else{
        Toast.makeText(context, "registro fallido", Toast.LENGTH_SHORT).show()
    }
}