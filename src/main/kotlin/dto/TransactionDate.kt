package dto

import common.DateTimeParser
import java.time.LocalDateTime
import java.time.ZoneOffset

class TransactionDate(val base: String) {

    @Transient
    val dateTime = DateTimeParser.parseToDate(base)
    @Transient
    val localDateTime = DateTimeParser.parseToLocalDate(base)

    val toEpoch get() = dateTime.time
    val hour get() = localDateTime.hour
}