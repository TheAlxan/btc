package data

import org.jetbrains.exposed.sql.Table

object TablesList {
    private val list = listOf<Table>(
        DelayedTable,
        BalanceTable
    )

    fun getTables() = list
}