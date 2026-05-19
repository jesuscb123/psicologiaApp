package dam2.tfg.psicologiaapp.presentation.ui.registro

object LimiteCaracteresRegistroPsicologo {
    const val NOMBRE = 50
    const val APELLIDOS = 100
    const val CORREO = 254
    const val DESCRIPCION = 1000
    const val NUMERO_COLEGIADO = 15
    const val ESPECIALIDAD = 80
    const val MAX_ESPECIALIDADES = 10

    fun mensajeMaximoCaracteres(max: Int): String = "Máximo $max caracteres"
}
