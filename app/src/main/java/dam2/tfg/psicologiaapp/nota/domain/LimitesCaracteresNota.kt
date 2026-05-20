package dam2.tfg.psicologiaapp.nota.domain

/** Debe coincidir con NotaRequest del API (asunto max 100, descripción max 2000). */
object LimitesCaracteresNota {
    const val ASUNTO = 100
    const val DESCRIPCION = 2000
}
