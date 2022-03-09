package common

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*

object DateTimeParser {
    private const val TIME_ZONE_ID_STRING = "+00:00"
    private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"

    private val zonedDateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT)

    fun parseToDate(dateString: String): ZonedDateTime = ZonedDateTime.parse(dateString, zonedDateFormat)
    fun epochToString(epoch: Long): String {
        return epochToString(epoch, ZoneId.of(TIME_ZONE_ID_STRING))
    }

    fun epochToString(epoch: Long, zoneId: ZoneId): String {
        val localDateTime = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.of(zoneId.id))!!
        val zoneDateTime = ZonedDateTime.ofInstant(localDateTime, ZoneOffset.of(zoneId.id), zoneId)
        return zonedDateFormat.format(zoneDateTime)
    }
}