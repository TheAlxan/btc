package data

import org.jetbrains.exposed.sql.*

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
        transactionForBalance {
            deleteAll()
        }
    }
}
