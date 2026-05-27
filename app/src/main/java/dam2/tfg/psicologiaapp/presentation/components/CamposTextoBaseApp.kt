package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.ui.theme.colorFondoCampoAzulApp

enum class EstiloCampoTextoApp {
    Normal,
    Minimal,
    /** Fondo azul suave, borde invisible hasta foco (inputs tipo Stitch). */
    ContenedorAlta,
    /** Formularios de registro: esquinas rectas y fondo suave. */
    Registro,
}

@Composable
fun CampoTextoBaseApp(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    habilitado: Boolean = true,
    soloLectura: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    iconoInicio: ImageVector? = null,
    contenidoDescripcionIconoInicio: String? = null,
    iconoFin: ImageVector? = null,
    contenidoDescripcionIconoFin: String? = null,
    alPulsarIconoFin: (() -> Unit)? = null,
    textoError: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    paddingExterno: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    estilo: EstiloCampoTextoApp = EstiloCampoTextoApp.Normal,
) {
    val esError = !textoError.isNullOrBlank()
    val fondoCampoAzul = colorFondoCampoAzulApp()
    val fondoCampoAzulDeshabilitado = fondoCampoAzul.copy(alpha = fondoCampoAzul.alpha * 0.55f)
    val colorContenedorHabilitado = fondoCampoAzul
    val colorContenedorDeshabilitado = fondoCampoAzulDeshabilitado
    val formaCampo: Shape = when (estilo) {
        EstiloCampoTextoApp.Registro -> RectangleShape
        EstiloCampoTextoApp.ContenedorAlta -> MaterialTheme.shapes.medium
        else -> MaterialTheme.shapes.large
    }
    val colores = when (estilo) {
        EstiloCampoTextoApp.Normal -> OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colorContenedorHabilitado,
            unfocusedContainerColor = colorContenedorHabilitado,
            disabledContainerColor = colorContenedorDeshabilitado,
            errorContainerColor = colorContenedorHabilitado,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
            disabledBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            errorTrailingIconColor = MaterialTheme.colorScheme.error
        )

        EstiloCampoTextoApp.Minimal -> OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            errorTrailingIconColor = MaterialTheme.colorScheme.error
        )

        EstiloCampoTextoApp.ContenedorAlta -> OutlinedTextFieldDefaults.colors(
            focusedContainerColor = fondoCampoAzul,
            unfocusedContainerColor = fondoCampoAzul,
            disabledContainerColor = fondoCampoAzulDeshabilitado,
            errorContainerColor = fondoCampoAzul,
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            disabledBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            errorTrailingIconColor = MaterialTheme.colorScheme.error
        )

        EstiloCampoTextoApp.Registro -> OutlinedTextFieldDefaults.colors(
            focusedContainerColor = fondoCampoAzul,
            unfocusedContainerColor = fondoCampoAzul,
            disabledContainerColor = fondoCampoAzulDeshabilitado,
            errorContainerColor = fondoCampoAzul,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            disabledBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            cursorColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
        )
    }

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .padding(paddingExterno),
        enabled = habilitado,
        readOnly = soloLectura,
        singleLine = singleLine,
        minLines = minLines,
        label = if (etiqueta.isNotBlank()) {
            { Text(etiqueta) }
        } else {
            null
        },
        placeholder = if (placeholder.isNullOrBlank()) null else {
            { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium) }
        },
        isError = esError,
        shape = formaCampo,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        leadingIcon = if (iconoInicio == null) null else {
            {
                Icon(
                    imageVector = iconoInicio,
                    contentDescription = contenidoDescripcionIconoInicio,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = if (iconoFin == null) null else {
            {
                if (alPulsarIconoFin == null) {
                    Icon(
                        imageVector = iconoFin,
                        contentDescription = contenidoDescripcionIconoFin,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    IconButton(onClick = alPulsarIconoFin) {
                        Icon(
                            imageVector = iconoFin,
                            contentDescription = contenidoDescripcionIconoFin,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        supportingText = if (!esError) null else {
            {
                Row(modifier = Modifier.padding(top = 2.dp)) {
                    Text(
                        text = textoError.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        colors = colores,
    )
}

@Composable
fun coloresOutlinedCampoBusquedaApp() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = colorFondoCampoAzulApp(),
    unfocusedContainerColor = colorFondoCampoAzulApp(),
    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
)

@Composable
fun coloresTextFieldCampoBusquedaApp() = TextFieldDefaults.colors(
    focusedContainerColor = colorFondoCampoAzulApp(),
    unfocusedContainerColor = colorFondoCampoAzulApp(),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
)

@Composable
fun CampoCorreoBaseApp(
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
    CampoTextoBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        etiqueta = etiqueta,
        placeholder = placeholder,
        habilitado = habilitado,
        textoError = textoError,
        keyboardType = KeyboardType.Email,
        paddingExterno = paddingExterno,
        estilo = estilo,
        iconoInicio = iconoInicio,
        contenidoDescripcionIconoInicio = contenidoDescripcionIconoInicio,
        modifier = modifier,
    )
}

@Composable
fun CampoContrasenaBaseApp(
    valor: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Contraseña",
    placeholder: String? = null,
    habilitado: Boolean = true,
    textoError: String? = null,
    paddingExterno: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    estilo: EstiloCampoTextoApp = EstiloCampoTextoApp.Normal,
    iconoInicio: ImageVector? = null,
    contenidoDescripcionIconoInicio: String? = null,
) {
    CampoTextoBaseApp(
        valor = valor,
        alCambiar = alCambiar,
        etiqueta = etiqueta,
        placeholder = placeholder,
        habilitado = habilitado,
        textoError = textoError,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation(),
        paddingExterno = paddingExterno,
        estilo = estilo,
        iconoInicio = iconoInicio,
        contenidoDescripcionIconoInicio = contenidoDescripcionIconoInicio,
        modifier = modifier,
    )
}

