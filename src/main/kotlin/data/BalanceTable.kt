package data

import common.DateTimeParser
import dto.BalanceRequest
import dto.Receipt
import dto.TransactionDate
import dto.TransactionList
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.math.BigDecimal
import java.util.*

object BalanceTable : BaseTable("balance") {

    val id = integer("id").autoIncrement()
    private val time = long("time").index()
    private val amount = decimal("amount", 13, 8)
    private val balance = decimal("balance", 13, 8)
    private val writeTime = long("writeTime")

    override val primaryKey = PrimaryKey(id, name = "PK_Balance_ID")

    fun saveTransaction(receipt: Receipt) {
        transactionForBalance {
            val lateRecords = DelayedTable.getDelayed()
            val latest = getLatestRecord(lateRecords)
            val currentBalance = latest?.currentBalance ?: BigDecimal.ZERO
            currentBalance.plus(receipt.amount)

            val id = insert {
                it[time] = receipt.transactionDate.toEpoch
                it[amount] = receipt.amount
                it[balance] = currentBalance
                it[writeTime] = Date().time
            }[id]

            if (receipt.transactionDate.toEpoch < (latest?.time?.toEpoch ?: 0))
                DelayedTable.addDelayed(id)
        }
    }

    fun getBalance(range: BalanceRequest): TransactionList {
        return transactionForBalance {
            val transactions = getRecordsInRange(range)
            var lateBalance = getLateRecordsSumBefore(range)
            val lateBalanceBk = lateBalance
            return@transactionForBalance TransactionList(
                transactions.map { it.toReceipt().apply { this.amount = this.amount.plus(lateBalance) } } +
                        Receipt(DateTimeParser.epochToString(range.startDate.toEpoch - 60 * 60, range.startDate.timeZone), lateBalanceBk)
            )
        }!!
    }

    fun fixLateRecord() {
        transactionForBalance {
            val lateRecords = DelayedTable.getDelayed() ?: return@transactionForBalance
            val selectedRecords = select { id.inList(lateRecords) }.orderBy(time to SortOrder.ASC).map { it[id] to it.toRecord() }
            selectedRecords.forEach { record ->
                TransactionManager.current().exec("UPDATE balance SET balance = balance + ${record.second.amount.toPlainString()} WHERE time > ${record.second.time.toEpoch} ")
            }
            DelayedTable.removeRecords()
        }
    }

    private fun getLatestRecord(lateRecords: List<Int>?): TransactionRecord? {
        return select { id.notInList(lateRecords ?: listOf()) }.orderBy(id to SortOrder.DESC).firstOrNull()?.toRecord()
    }

    private fun getLateRecordsSumBefore(range: BalanceRequest): BigDecimal {
        val lateRecords = DelayedTable.getDelayed() ?: return BigDecimal.ZERO
        val selectedRecords = select { id.inList(lateRecords) }.map { it.toRecord() }
        return selectedRecords.filter { it.time.toEpoch < range.startDate.toEpoch }
            .map { it.amount }
            .reduceOrNull { acc, i -> acc.plus(i) } ?: BigDecimal.ZERO
    }

    private fun getRecordsInRange(range: BalanceRequest): List<TransactionRecord> {
        return select {
            (time.greaterEq(range.startDate.toEpoch)) and (time.lessEq(range.endDate.toEpoch))
        }.orderBy(time to SortOrder.ASC).map { it.toRecord() }
    }

    private fun ResultRow.toRecord(): TransactionRecord {
        return TransactionRecord(
            this[id],
            TransactionDate.from(this[time]),
            this[amount],
            this[balance]
        )
    }

    private fun TransactionRecord.toReceipt(): Receipt {
        return Receipt(DateTimeParser.epochToString(this.time.toEpoch, this.time.timeZone), this.currentBalance)
    }

    private data class TransactionRecord(
        val id: Int,
        val time: TransactionDate,
        val amount: BigDecimal,
        var currentBalance: BigDecimal,
    ) {

        init {
            amount.setScale(8)
            currentBalance.setScale(8)
        }
        
        fun sum(other: BigDecimal) {
            currentBalance = currentBalance.plus(other)
        }

        fun setBalance(newBalance: BigDecimal) {
            currentBalance = newBalance
        }
    }
}