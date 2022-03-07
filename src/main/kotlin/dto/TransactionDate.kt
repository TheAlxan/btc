package dto

import common.DateTimeParser

class TransactionDate(val base: String) {

    @Transient
    val dateTime = DateTimeParser.parseToDate(base)
    @Transient
    val localDateTime = DateTimeParser.parseToLocalDate(base)

    val toEpoch get() = dateTime.time
    val hour get() = localDateTime.hour

    companion object {
        fun from(epoch: Long): TransactionDate {
            return TransactionDate(DateTimeParser.epochToString(epoch))
        }
    }
}