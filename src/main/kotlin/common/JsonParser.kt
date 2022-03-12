package common

import com.google.gson.Gson
import dto.BaseDto

object JsonParser {
    private val gson = Gson()

    fun getParser() = gson
    fun <T> parseTo(json: String, clazz: Class<T>): T = getParser().fromJson(json, clazz).apply { if (this is BaseDto) initialize() }
    fun serialize(data: Any): String? {
        return getParser().toJson(data)
    }
}