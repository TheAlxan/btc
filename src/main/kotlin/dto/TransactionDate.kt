package dto

import common.DateTimeParser
import java.time.ZoneId

class TransactionDate(base: String) {

    @Transient
    val dateTime = DateTimeParser.parseToDate(base)

    val toEpoch get() = dateTime.toEpochSecond()
    val timeZone: ZoneId get() = dateTime.zone

    companion object {
        fun from(epoch: Long): TransactionDate {
            return TransactionDate(DateTimeParser.epochToString(epoch))
        }
    }
}