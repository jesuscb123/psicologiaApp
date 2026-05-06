package dam2.tfg.psicologiaapp.notificaciones.domain.repository

interface NotificacionesRepository {

    /**
     * Registra (o actualiza) el token FCM del usuario autenticado en el backend.
     * Idempotente: el backend gestiona inserciones nuevas y reasignaciones.
     */
    suspend fun registrarTokenActual(token: String): Result<Unit>

    /**
     * Da de baja el [token] FCM en el backend antes de cerrar sesión, para evitar
     * que el dispositivo siga recibiendo pushes destinados al usuario que sale.
     */
    suspend fun darDeBajaToken(token: String): Result<Unit>

    /**
     * Obtiene el token FCM actual de la instalación. Suspende hasta que Firebase lo entrega.
     */
    suspend fun obtenerTokenFcmActual(): Result<String>

    /**
     * Notifica al destinatario del chat. Se llama desde el cliente justo después de escribir
     * el mensaje en Realtime Database con éxito.
     */
    suspend fun notificarMensajeChat(chatId: String, vistaPreviaTexto: String): Result<Unit>
}
