package data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import config.AppConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

object DatabaseConnector {
    const val DATABASE_NAME = "anymind"
    private lateinit var dataSource: DataSource
    private lateinit var database: Database

    fun connect() {
        val databaseConfig = AppConfig.getInstance().database
        val config = HikariConfig()
        config.username = databaseConfig.username
        config.password = databaseConfig.password
        config.jdbcUrl = databaseConfig.url
        dataSource = HikariDataSource(config)
        database = Database.connect(dataSource)
        setUpDatabase()
    }

    private fun setUpDatabase() {
        transaction(database) {
            SchemaUtils.createDatabase(DATABASE_NAME)

            TablesList.getTables().forEach { table ->
                SchemaUtils.createMissingTablesAndColumns(table)
            }
        }
    }

    fun getDatabase() = database


}