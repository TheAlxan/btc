package data

import common.DateTimeParser
import dto.BalanceRequest
import dto.Receipt
import dto.TransactionDate
import dto.TransactionList
import org.jetbrains.exposed.sql.*
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
            val last = getLatestRecord(lateRecords)
            val before = getLatestRecordBefore(receipt.transactionDate, lateRecords)
            val currentBalance = (before?.currentBalance ?: BigDecimal.ZERO)
                .plus(receipt.amount)

            val id = insert {
                it[time] = receipt.transactionDate.toEpoch
                it[amount] = receipt.amount
                it[balance] = currentBalance
                it[writeTime] = Date().time
            }[id]

            if (receipt.transactionDate.toEpoch < (last?.time?.toEpoch ?: 0))
                DelayedTable.addDelayed(id)
        }
    }

    fun getBalance(range: BalanceRequest): TransactionList {
        return transactionForBalance {
            val lateRecords = DelayedTable.getDelayed()
            val lastBalance = getLatestRecordBefore(range.startDate, lateRecords)?.currentBalance ?: BigDecimal.ZERO
            val transactions = getRecordsInRange(range)
            var lateBalance = getLateRecordsSumBefore(range.startDate).plus(lastBalance)
            val lateBalanceBk = lateBalance
            return@transactionForBalance TransactionList(
                transactions.map {
                    it.toReceipt().apply { this.amount = this.amount.plus(lateBalance); lateBalance = this.amount }
                } +
                        Receipt(
                            DateTimeParser.epochToString(
                                range.startDate.toEpoch - 60 * 60,
                                range.startDate.timeZone
                            ), lateBalanceBk
                        )
            )
        }!!
    }

    fun fixLateRecord() {
        transactionForBalance {
            val lateRecords = DelayedTable.getDelayed() ?: return@transactionForBalance
            val selectedRecords =
                select { id.inList(lateRecords) }.orderBy(time to SortOrder.ASC).map { it[id] to it.toRecord() }
            selectedRecords.forEach { record ->
                update({ time.greater(record.second.time.toEpoch) }) {
                    with(SqlExpressionBuilder) {
                        it[balance] = balance.plus(record.second.amount)
                    }
                }
            }
            DelayedTable.removeRecords()
        }
    }

    private fun getLatestRecord(lateRecords: List<Int>?): TransactionRecord? {
        return select {
            (id.notInList(lateRecords ?: listOf()))
        }.orderBy(id to SortOrder.DESC).firstOrNull()?.toRecord()
    }

    private fun getLatestRecordBefore(currentTime: TransactionDate, lateRecords: List<Int>?): TransactionRecord? {
        return select {
            (id.notInList(lateRecords ?: listOf())) and (time.lessEq(currentTime.toEpoch))
        }.orderBy(time to SortOrder.DESC).firstOrNull()?.toRecord()
    }

    private fun getLateRecordsSumBefore(time: TransactionDate): BigDecimal {
        val lateRecords = DelayedTable.getDelayed() ?: return BigDecimal.ZERO
        val selectedRecords = select { id.inList(lateRecords) }.map { it.toRecord() }
        return selectedRecords.filter { it.time.toEpoch < time.toEpoch }
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
        return Receipt(DateTimeParser.epochToString(this.time.toEpoch, this.time.timeZone), this.amount)
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
    }
}