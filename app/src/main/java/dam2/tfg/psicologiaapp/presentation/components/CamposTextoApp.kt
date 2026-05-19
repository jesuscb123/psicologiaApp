package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CampoCorreoApp(
    valor: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Correo",
    placeholder: String? = null,
    habilitado: Boolean = true,
    textoError: String? = null,
    paddingExterno: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    estilo: EstiloCampoTextoApp = EstiloCampoTextoApp.Normal,
    iconoInicio: ImageVector? = null,
    contenidoDescripcionIconoInicio: String? = null,
) {
    CampoCorreoBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        modifier = modifier,
        etiqueta = etiqueta,
        placeholder = placeholder,
        habilitado = habilitado,
        textoError = textoError,
        paddingExterno = paddingExterno,
        estilo = estilo,
        iconoInicio = iconoInicio,
        contenidoDescripcionIconoInicio = contenidoDescripcionIconoInicio,
    )
}

@Composable
fun CampoContrasenaApp(
    valor: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Contraseña",
    placeholder: String? = null,
    habilitado: Boolean = true,
    paddingExterno: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    estilo: EstiloCampoTextoApp = EstiloCampoTextoApp.Normal,
    iconoInicio: ImageVector? = null,
    contenidoDescripcionIconoInicio: String? = null,
) {
    CampoContrasenaBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        modifier = modifier,
        etiqueta = etiqueta,
        placeholder = placeholder,
        habilitado = habilitado,
        paddingExterno = paddingExterno,
        estilo = estilo,
        iconoInicio = iconoInicio,
        contenidoDescripcionIconoInicio = contenidoDescripcionIconoInicio,
    )
}

@Composable
fun CampoTextoApp(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    habilitado: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    textoError: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    paddingExterno: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    estilo: EstiloCampoTextoApp = EstiloCampoTextoApp.Normal,
) {
    CampoTextoBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        etiqueta = etiqueta,
        placeholder = placeholder,
        modifier = modifier,
        habilitado = habilitado,
        singleLine = singleLine,
        minLines = minLines,
        textoError = textoError,
        keyboardType = keyboardType,
        visualTransformation = visualTransformation,
        paddingExterno = paddingExterno,
        estilo = estilo,
    )
}

