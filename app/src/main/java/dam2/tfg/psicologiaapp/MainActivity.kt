package dam2.tfg.psicologiaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dam2.tfg.psicologiaapp.ui.main.MainScreen
import dam2.tfg.psicologiaapp.ui.theme.PsicologiaAppTheme
import dam2.tfg.psicologiaapp.ui.screens.auth.IniciarSesionPantalla
import dam2.tfg.psicologiaapp.ui.screens.auth.RegistroPantalla
import dam2.tfg.psicologiaapp.utils.auth.AuthManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                PsicologiaAppTheme {
                    MainScreen()
                }
            }
        }
}


/*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IniciarAPP(){
    val LocalContext = LocalContext.current
    var navController = rememberNavController()
    var auth = AuthManager(LocalContext)

    PsicologiaAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text("AppPsicología")
            })
        }
            ) { innerPadding ->
            NavHost(navController, startDestination = "iniciarSesion", modifier = Modifier.padding(innerPadding)) {
                composable("iniciarSesion") { IniciarSesionPantalla(navController, auth) }
                composable("registro") { RegistroPantalla(navController, auth) }
            }
        }
    }
}

 */