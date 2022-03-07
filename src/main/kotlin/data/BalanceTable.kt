package data

import common.DateTimeParser
import dto.BalanceRequest
import dto.Receipt
import dto.TransactionDate
import dto.TransactionList
import exception.Data
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

object BalanceTable : BaseTable("balance") {

    private enum class RecordStatus(val code: Int) {
        CLEAR(0),
        LATE(1),
        FIXED(2);

        companion object {
            fun from(code: Int): RecordStatus {
                return values().firstOrNull { it.code == code } ?: throw Data.InvalidTransactionRecordStatus()
            }

            fun getRecordStatus(current: Receipt, previous: TransactionRecord?): Short {
                return when {
                    current.transactionDate.toEpoch < (previous?.time?.toEpoch ?: 0) -> LATE.code.toShort()
                    else -> CLEAR.code.toShort()
                }
            }
        }
    }

    val id = integer("id").autoIncrement()
    val time = long("time").index()
    val amount = decimal("amount", 13, 8)
    val balance = decimal("balance", 13, 8).index()
    val status = short("status").index()


    override val primaryKey = PrimaryKey(id, name = "PK_Balance_ID")

    fun saveTransaction(receipt: Receipt) {
        transactionForBalance {
            val latest = getLatestRecord()
            val latestClear = if (latest?.status == RecordStatus.CLEAR) latest else getLatestClearRecord()

            val currentBalance = latestClear?.balance ?: BigDecimal.ZERO
            currentBalance.plus(receipt.amount)
            if (latest?.isLate() == true)
                currentBalance.plus(latest.amount)


            insert {
                it[time] = receipt.transactionDate.toEpoch
                it[amount] = receipt.amount
                it[balance] = currentBalance
                it[status] = RecordStatus.getRecordStatus(receipt, latestClear)
            }
        }
    }

    fun getBalance(range: BalanceRequest): TransactionList {
        return transactionForBalance {
            val transactions = getRecordsInRange(range)
            val lateBalance = getLateRecordsSumBefore(range.startDate)
            transactions.forEach { it.balance.plus(lateBalance) }
            return@transactionForBalance TransactionList(transactions.map { it.toReceipt() })
        }!!
    }

    private fun fixLateRecord(record: TransactionRecord) {
        update({
            id.eq(record.id)
        }) {
            it[status] = RecordStatus.FIXED.code.toShort()
        }
    }

    private fun getLatestRecord(): TransactionRecord? {
        return selectAll().orderBy(time to SortOrder.DESC).firstOrNull()?.toRecord()
    }

    private fun getLatestClearRecord(): TransactionRecord? {
        return select {
            (status.eq(RecordStatus.CLEAR.code.toShort()))
        }.orderBy(time to SortOrder.DESC).firstOrNull()?.toRecord()
    }

    private fun getLateRecordsSumBefore(transactionDate: TransactionDate): BigDecimal {
        return slice(balance).select {
            (time.lessEq(transactionDate.toEpoch)) and (status.eq(RecordStatus.LATE.code.toShort()))
        }.map { it[balance] }.reduceOrNull { acc, it -> acc.plus(it) } ?: BigDecimal.ZERO
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
            this[balance],
            RecordStatus.from(this[status].toInt())
        )
    }

    private fun TransactionRecord.toReceipt(): Receipt {
        return Receipt(DateTimeParser.epochToString(this.time.toEpoch), this.balance)
    }

    private data class TransactionRecord(
        val id: Int,
        val time: TransactionDate,
        val amount: BigDecimal,
        val balance: BigDecimal,
        val status: RecordStatus
    ) {
        fun isLate() = status == RecordStatus.LATE
    }
}

/**

time, amount
time, balance, dmged


10, 1   10, 11
20, 1   20, 12
30, 1   30, 13
40, 1   40, 14
11, 1   11, 12, l
50, 1   50, 16
60, 1   60, 17
31, 1   31, 14
70, 1   70, 19



 */