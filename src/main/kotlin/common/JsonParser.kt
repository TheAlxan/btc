package common

import dto.Receipt
import com.google.gson.Gson
import dto.BaseDto

object JsonParser {
    private val gson = Gson()

    fun getParser() = gson
    fun <T: BaseDto> parseTo(json: String, clazz: Class<T>): T = getParser().fromJson(json, clazz).apply { this.initialize() }
}