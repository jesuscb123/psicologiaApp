package dam2.tfg.psicologiaapp.psicologo.data.local

import androidx.room.TypeConverter
import org.json.JSONArray

class StringListConverter {

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(value)
            List(array.length()) { i -> array.getString(i) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun toString(list: List<String>?): String {
        if (list.isNullOrEmpty()) return "[]"
        return JSONArray(list).toString()
    }
}
