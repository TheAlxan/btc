package dto

import com.google.gson.annotations.SerializedName

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
        return startDate.toEpoch
    }

    fun getEndingHour(): Long {
        return endDate.toEpoch
    }
}