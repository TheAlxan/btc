package dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Calendar

class BalanceRequest(@SerializedName("startDatetime") private val startDateTime: String,
                     @SerializedName("endDatetime") private val endDateTime: String): BaseDto() {
    @Transient
    lateinit var startDate: TransactionDate

    @Transient
    lateinit var endDate: TransactionDate

    override fun initialize() {
        startDate = TransactionDate(startDateTime)
        endDate = TransactionDate(endDateTime)
    }

    fun getStartingHour(): Long {
        val minutesToReduce = startDate.localDateTime.minute
        val secondsToReduce = startDate.localDateTime.second
        return startDate.toEpoch - minutesToReduce * 1000 * 60 - secondsToReduce * 1000
    }

    fun getEndingHour(): Long {
        val minutesToReduce = endDate.localDateTime.minute
        val secondsToReduce = endDate.localDateTime.second
        return endDate.toEpoch - minutesToReduce * 1000 * 60 - secondsToReduce * 1000
    }
}