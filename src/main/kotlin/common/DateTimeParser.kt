package common

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object DateTimeParser {
    const val TIME_ZONE_ID_STRING = "GMT"
    val TIME_ZONE_ID = ZoneId.of(TIME_ZONE_ID_STRING)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

    fun parseToDate(dateString: String): Date = dateFormat.parse(dateString)
    fun parseWithTimeZoneToLocalDate(dateString: String, zoneId: String): LocalDateTime = LocalDateTime.ofInstant(parseToDate(dateString).toInstant(), TimeZone.getTimeZone(zoneId).toZoneId())
    fun parseToLocalDate(dateString: String): LocalDateTime = parseWithTimeZoneToLocalDate(dateString, TIME_ZONE_ID_STRING)
    fun epochToString(epoch: Long): String = dateFormat.format(Date.from(Instant.ofEpochMilli(epoch)))
}