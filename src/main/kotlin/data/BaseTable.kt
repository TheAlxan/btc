package data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseTable(name: String): Table(name) {
    fun <T: Any?> transactionForBalance(block: () -> T?): T? {
        return transaction(DatabaseConnector.getDatabase()) {
            return@transaction block()
        }
    }
}