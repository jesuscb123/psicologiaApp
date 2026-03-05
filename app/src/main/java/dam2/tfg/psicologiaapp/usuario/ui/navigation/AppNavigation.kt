package dam2.tfg.psicologiaapp.usuario.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dam2.tfg.psicologiaapp.usuario.ui.screens.RegisterScreen
import dam2.tfg.psicologiaapp.usuario.ui.screen.RoleScreen
import dam2.tfg.psicologiaapp.usuario.ui.screen.StartScreen
import dam2.tfg.psicologiaapp.usuario.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start") {

        // 1. Pantalla Inicial
        composable("start") {
            StartScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRoles = { navController.navigate("role") }
            )
        }

        // 2. Pantalla de Selección de Rol
        composable("role") {
            RoleScreen(
                onRoleSelected = { role ->
                    // Viajamos a la pantalla de registro pasándole el rol en la URL
                    navController.navigate("register/$role")
                }
            )
        }

        // 3. Pantalla de Login (Un placeholder por ahora)
        composable("login") {
            Text("Pantalla de Login en construcción...")
        }

        // 4. Pantalla de Registro (Un placeholder por ahora)
        composable("register/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "paciente"

            // Hilt te da el ViewModel ya inyectado con todo el core!
            val usuarioViewModel: UsuarioViewModel = hiltViewModel()

            RegisterScreen(
                role = role,
                viewModel = usuarioViewModel,
                onNavigateToHome = {
                    // Aquí navegarías a la Home de tu app después de registrarse
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Bienvenido! Registro completado con éxito.")
            }
        }
    }
}