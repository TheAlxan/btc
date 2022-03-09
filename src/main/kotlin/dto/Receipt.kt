package dto

import com.google.gson.annotations.SerializedName
import common.DateTimeParser
import exception.Common
import java.math.BigDecimal
import java.time.ZoneId

class Receipt(@SerializedName("datetime") private val dateTime: String, var amount: BigDecimal): BaseDto() {
    @Transient
    lateinit var transactionDate: TransactionDate

    init {
        transactionDate = TransactionDate(dateTime)
    }

    override fun initialize() {
        transactionDate = TransactionDate(dateTime)
    }

    companion object {
        fun of(epochSeconds: Long, amount: BigDecimal): Receipt {
            return Receipt(DateTimeParser.epochToString(epochSeconds), amount)
        }

        fun of(epochSeconds: Long, zoneId: ZoneId, amount: BigDecimal): Receipt {
            return Receipt(DateTimeParser.epochToString(epochSeconds, zoneId), amount)
        }
    }
}