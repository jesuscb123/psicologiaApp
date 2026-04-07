package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoCorreoApp(
    valor: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Correo",
    habilitado: Boolean = true,
) {
    CampoCorreoBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        modifier = modifier,
        etiqueta = etiqueta,
        habilitado = habilitado,
    )
}

@Composable
fun CampoContrasenaApp(
    valor: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Contraseña",
    habilitado: Boolean = true,
) {
    CampoContrasenaBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        modifier = modifier,
        etiqueta = etiqueta,
        habilitado = habilitado,
    )
}

@Composable
fun CampoTextoApp(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    CampoTextoBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        etiqueta = etiqueta,
        modifier = modifier,
        habilitado = habilitado,
        singleLine = singleLine,
        minLines = minLines,
        keyboardType = keyboardType,
        visualTransformation = visualTransformation,
    )
}

