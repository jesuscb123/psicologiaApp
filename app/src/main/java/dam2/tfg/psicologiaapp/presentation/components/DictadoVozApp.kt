package dam2.tfg.psicologiaapp.presentation.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dam2.tfg.psicologiaapp.R
import java.util.Locale

fun combinarTextoDictado(textoActual: String, textoReconocido: String): String {
    val reconocido = textoReconocido.trim()
    if (reconocido.isEmpty()) return textoActual
    return if (textoActual.isBlank()) reconocido else "$textoActual $reconocido"
}

@Composable
fun rememberControladorDictadoVoz(
    valorActual: String,
    alCambiar: (String) -> Unit,
    habilitado: Boolean = true,
): () -> Unit {
    val context = LocalContext.current
    val valorActualRef = rememberUpdatedState(valorActual)
    val alCambiarRef = rememberUpdatedState(alCambiar)
    val habilitadoRef = rememberUpdatedState(habilitado)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultados = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val textoReconocido = resultados?.firstOrNull().orEmpty()
            if (textoReconocido.isNotBlank()) {
                alCambiarRef.value(combinarTextoDictado(valorActualRef.value, textoReconocido))
            }
        }
    }

    return {
        if (habilitadoRef.value) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES").toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.dictado_voz_prompt))
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                launcher.launch(intent)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.dictado_voz_no_disponible),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}

@Composable
fun IconoDictadoVozApp(
    onClick: () -> Unit,
    habilitado: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.Mic,
            contentDescription = stringResource(R.string.dictado_voz_cd),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun TrailingIconDictadoVozApp(
    valor: String,
    alCambiar: (String) -> Unit,
    habilitado: Boolean = true,
) {
    val iniciarDictado = rememberControladorDictadoVoz(
        valorActual = valor,
        alCambiar = alCambiar,
        habilitado = habilitado,
    )
    IconoDictadoVozApp(
        onClick = iniciarDictado,
        habilitado = habilitado,
    )
}
