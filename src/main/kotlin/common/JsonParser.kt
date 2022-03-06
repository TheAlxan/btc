package common

import Receipt
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime

object JsonParser {
    private val gson = Gson()

    fun getParser() = gson
    fun parseToReceipt(json: String): Receipt = getParser().fromJson(json, Receipt::class.java).apply { this.initialize() }
}