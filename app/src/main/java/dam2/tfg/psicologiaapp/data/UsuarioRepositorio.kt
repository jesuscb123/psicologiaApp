package dam2.tfg.psicologiaapp.data

object UsuarioRepositorio {
    var idToken: String? = null

    fun guardarToken(token: String){
        idToken = token
    }

    fun limpiarToken(){
        idToken = null
    }

    fun obtenerAuthHeader(): String?{
        return idToken?.let {
            "Bearer $it"
        }
    }
}