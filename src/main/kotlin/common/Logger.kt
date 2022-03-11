package common

import org.apache.log4j.Logger

class Logger(clazz: Class<*>) {
    private val logger = Logger.getLogger(clazz)

    fun log(msg: String) {
        logger.info(msg)
    }

    fun error(msg: String) {
        logger.error(msg)
    }
}