package dto

import com.google.gson.annotations.SerializedName
import exception.Common
import java.math.BigDecimal

class Receipt(@SerializedName("datetime") private val dateTime: String, val amount: BigDecimal): BaseDto() {
    @Transient
    lateinit var transactionDate: TransactionDate

    override fun initialize() {
        transactionDate = TransactionDate(dateTime)
    }
}