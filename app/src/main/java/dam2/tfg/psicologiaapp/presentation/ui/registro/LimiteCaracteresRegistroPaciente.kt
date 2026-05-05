package dam2.tfg.psicologiaapp.presentation.ui.registro

object LimiteCaracteresRegistroPaciente {
    const val NOMBRE = 50
    const val APELLIDOS = 100
    const val CORREO = 254

    fun mensajeMaximoCaracteres(max: Int): String = "Máximo $max caracteres"
}

