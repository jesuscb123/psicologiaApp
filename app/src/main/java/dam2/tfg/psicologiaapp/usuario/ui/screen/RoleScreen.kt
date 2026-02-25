package dam2.tfg.psicologiaapp.usuario.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RoleScreen(
    onRoleSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿Cómo vas a usar la aplicación?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = { onRoleSelected("paciente") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Soy Paciente")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onRoleSelected("psicologo") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Soy Psicólogo")
        }
    }
}
