package controller.admin

import controller.BaseController
import data.DatabaseConnector
import data.DelayedTable
import data.TablesList
import exception.Admin
import exception.Common
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AdminHandler : BaseController<Any>(Any::class.java) {

    private object Commands {
        private val methods = mapOf(
            "dropDatabase" to ::dropDatabase,
            "delayedCount" to ::delayedCount
        )

        fun runCommand(cmd: String): Any? {
            return (methods[cmd] ?: throw Admin.CommandNotFoundException()).invoke()
        }

        private fun dropDatabase() {
            transaction(DatabaseConnector.getDatabase()) {
                TablesList.getTables().forEach { SchemaUtils.drop(it) }
                TablesList.getTables().forEach { SchemaUtils.createMissingTablesAndColumns(it) }
            }
        }

        private fun delayedCount(): Long {
            return transaction(DatabaseConnector.getDatabase()) {
                return@transaction DelayedTable.selectAll().count()
            }
        }
    }
    override fun guard(request: Request<Any>) {
        require(getPathParameter(request.context, "cmd")?.isNotEmpty() ?: false) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(request: Request<Any>): Any? {
        return Commands.runCommand(getPathParameter(request.context, "cmd")!!)
    }
}