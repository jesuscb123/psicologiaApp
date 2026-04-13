package dam2.tfg.psicologiaapp.presentation.components



import androidx.activity.compose.LocalOnBackPressedDispatcherOwner

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.IconButtonDefaults

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp



@Composable

fun EncabezadoUsuarioApp(
    nombreParaSaludo: String? = null,

    mostrarFlechaAtras: Boolean = false,

    alVolver: (() -> Unit)? = null,

    nombreUsuario: String,

    fotoPerfilUrl: String?,

    revisionCacheFoto: Long = 0L,

    alAbrirMenuPerfil: () -> Unit,

    sobreGradiente: Boolean = true,

    modifier: Modifier = Modifier,

) {

    val dispatcher =

        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val nombreEnSaludo = nombreParaSaludo?.takeIf { it.isNotBlank() } ?: nombreUsuario

    val colorTextoPrincipal = colorTextoEncabezado(sobreGradiente, principal = true)

    Row(

        modifier = modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.spacedBy(8.dp),

    ) {

        if (mostrarFlechaAtras) {

            val accionVolver: () -> Unit =

                alVolver ?: { dispatcher?.onBackPressed() }

            IconButton(

                onClick = accionVolver,

                colors = IconButtonDefaults.iconButtonColors(

                    contentColor = colorTextoPrincipal,

                ),

            ) {

                Icon(

                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription = "Volver",

                )

            }

        }


        Text(
            text = "Bienvenido, $nombreEnSaludo",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorTextoPrincipal,
        )



        AccionesBarraMenuPerfilPaciente(

            nombreUsuario = nombreUsuario,

            fotoPerfilUrl = fotoPerfilUrl,

            revisionCacheFoto = revisionCacheFoto,

            alAbrirMenu = alAbrirMenuPerfil,

        )

    }

}



@Composable

private fun colorTextoEncabezado(sobreGradiente: Boolean, principal: Boolean): Color {

    return if (sobreGradiente) {

        if (principal) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
        }

    } else {

        if (principal) {

            MaterialTheme.colorScheme.onSurface

        } else {

            MaterialTheme.colorScheme.onSurfaceVariant

        }

    }

}


