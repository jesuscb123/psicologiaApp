package dam2.tfg.psicologiaapp.usuario.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dam2.tfg.psicologiaapp.usuario.ui.screen.RoleScreen
import dam2.tfg.psicologiaapp.usuario.ui.screen.StartScreen

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
            Text("Pantalla de Registro para: $role")
        }
    }
}