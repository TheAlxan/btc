import java.math.BigDecimal

class Receipt(private val date: String, val amount: BigDecimal) {
    @Transient
    lateinit var transactionDate: TransactionDate

    fun initialize() {
        require(amount > BigDecimal.ZERO) { "Amount should be positive" }
        transactionDate = TransactionDate(date)
    }

}