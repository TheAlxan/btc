package data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import common.Logger
import config.AppConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

object DatabaseConnector {
    private const val DATABASE_NAME = "anymind"
    private val logger = Logger(DatabaseConnector::class.java)
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
        logger.log("Database connection established")
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