package data

import common.Logger
import config.AppConfig
import java.util.Timer
import java.util.TimerTask

object DatabaseMaintainer {
    private val logger = Logger(DatabaseMaintainer::class.java)

    fun initialize() {
        val delay = AppConfig.getInstance().database.fixDelay
        Timer().schedule(MaintainTask(), delay, delay)
        logger.log("Database maintainer initialized.")
    }

    private class MaintainTask : TimerTask() {
        override fun run() {
            logger.log("Fixing late records in database.")
            BalanceTable.fixLateRecord()
        }

    }
}