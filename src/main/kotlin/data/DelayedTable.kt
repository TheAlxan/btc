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

object DelayedTable : BaseTable("delayed") {

    private val id = integer("id").autoIncrement()
    private val record = reference("record", BalanceTable.id)

    override val primaryKey = PrimaryKey(id, name = "PK_Delayed_ID")

    fun addDelayed(recordId: Int) {
        transactionForBalance {
            insert { it[record] = recordId }
        }
    }
    fun getDelayed(): List<Int>? {
        return transactionForBalance {
            return@transactionForBalance selectAll().map { it[record] }
        }
    }

    fun removeRecords() {
        deleteAll()
    }
}
