package dam2.tfg.psicologiaapp.presentation.ui.notificaciones

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Solicita al usuario el permiso runtime POST_NOTIFICATIONS la primera vez que se compone.
 *
 * - En API < 33 el permiso no existe y se devuelve concedido automáticamente.
 * - Si el usuario lo deniega, no insistimos: el resto de la app sigue funcionando, solo
 *   no se mostrarán notificaciones hasta que cambie el ajuste manualmente.
 */
@Composable
fun SolicitarPermisoNotificacionesUnaVez() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { /* sin acción adicional: si se concede, el sistema ya envía notificaciones */ }

    val yaConcedido = remember {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        if (!yaConcedido && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
