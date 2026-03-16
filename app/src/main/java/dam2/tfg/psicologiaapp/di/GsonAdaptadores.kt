package dam2.tfg.psicologiaapp.di

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dam2.tfg.psicologiaapp.paciente.data.remote.PacientePerfilResponseDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoPerfilResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto
import java.lang.reflect.Type

/**
 * Adaptadores Gson para DTOs polimórficos que usan la propiedad "rol"
 * como discriminador (alineado con el backend Jackson).
 */

object UsuarioResponseDtoDeserializer : JsonDeserializer<UsuarioResponseDto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): UsuarioResponseDto {
        val obj = json.asJsonObject
        val rol = obj.get("rol")?.asString ?: "SIN_ROL"
        return when (rol) {
            "PSICOLOGO" -> context.deserialize(json, PsicologoResponseDto::class.java)
            "PACIENTE" -> context.deserialize(json, PacienteResponseDto::class.java)
            else -> context.deserialize(json, UsuarioBasicoResponseDto::class.java)
        }
    }
}

object UsuarioPerfilResponseDtoDeserializer : JsonDeserializer<UsuarioPerfilResponseDto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): UsuarioPerfilResponseDto {
        val obj = json.asJsonObject
        val rol = obj.get("rol")?.asString ?: "SIN_ROL"
        return when (rol) {
            "PSICOLOGO" -> context.deserialize(json, PsicologoPerfilResponseDto::class.java)
            "PACIENTE" -> context.deserialize(json, PacientePerfilResponseDto::class.java)
            else -> context.deserialize(json, UsuarioPerfilBasicoResponseDto::class.java)
        }
    }
}

object UsuarioRequestDtoSerializer : JsonSerializer<UsuarioRequestDto> {
    override fun serialize(
        src: UsuarioRequestDto,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = context.serialize(src, src::class.java)
}
