package controller.admin

import controller.BaseController
import data.BalanceTable
import data.DatabaseConnector
import data.TablesList
import dto.Receipt
import exception.Admin
import exception.Common
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import service.BalanceService
import java.math.BigDecimal

class AdminHandler : BaseController<Any>(Any::class.java) {

    private object Commands {
        private val methods = mapOf(
            "dropDatabase" to ::dropDatabase
        )

        fun runCommand(cmd: String) {
            methods.get(cmd)?.invoke() ?: throw Admin.CommandNotFoundException()
        }

        private fun dropDatabase() {
            transaction(DatabaseConnector.getDatabase()) {
                TablesList.getTables().forEach { SchemaUtils.drop(it) }
                TablesList.getTables().forEach { SchemaUtils.createMissingTablesAndColumns(it) }
            }
        }
    }
    override fun guard(request: Request<Any>) {
        require(getPathParameter(request.context, "cmd")?.isNotEmpty() ?: false) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(request: Request<Any>) {
        Commands.runCommand(getPathParameter(request.context, "cmd")!!)
    }
}