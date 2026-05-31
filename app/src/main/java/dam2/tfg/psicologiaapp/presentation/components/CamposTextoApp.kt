package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
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
    habilitarDictadoVoz: Boolean = false,
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
        habilitarDictadoVoz = habilitarDictadoVoz,
    )
}

/**
 * Campo de texto multilinea con contador de caracteres y scroll interno.
 * El campo mantiene su tamaño fijo (no crece) y permite scroll interno automático.
 * Ideal para descripciones largas con límite de caracteres.
 */
@Composable
fun CampoTextoConContadorApp(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    limiteCaracteres: Int,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    habilitado: Boolean = true,
    textoError: String? = null,
    maxLineas: Int = 6,
    paddingExterno: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    estilo: EstiloCampoTextoApp = EstiloCampoTextoApp.Normal,
    habilitarDictadoVoz: Boolean = false,
) {
    Column(
        modifier = modifier,
    ) {
        CampoTextoBaseApp(
            valor = valor,
            alCambiar = alCambiar,
            etiqueta = etiqueta,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
            habilitado = habilitado,
            singleLine = false,
            minLines = 4,
            maxLines = maxLineas,
            textoError = textoError,
            paddingExterno = paddingExterno,
            estilo = estilo,
            habilitarDictadoVoz = habilitarDictadoVoz,
        )

        // Contador de caracteres
        Text(
            text = "${valor.length}/$limiteCaracteres",
            style = MaterialTheme.typography.bodySmall,
            color = if (valor.length >= limiteCaracteres) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (valor.length >= limiteCaracteres) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 14.dp, top = 4.dp),
        )
    }
}
