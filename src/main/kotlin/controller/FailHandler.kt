package controller

import com.google.gson.JsonSyntaxException
import common.Logger
import exception.BaseException
import exception.Input
import io.vertx.ext.web.RoutingContext
import java.time.format.DateTimeParseException
import java.util.*

class FailHandler(clazz: Class<*>): BaseHandler<String>(String::class.java) {
    private val logger = Logger(clazz)
    override fun handle(ctx: RoutingContext) {
        logger.error("Failed request from ${ctx.request().remoteAddress()} at ${Date()}")
        val exception = ctx.failure()
        val response = ctx.response()
        when (exception) {
            is BaseException -> {
                response.statusCode = exception.code
                response.end(getFailMessage(exception))
            }
            is JsonSyntaxException -> {
                val clientError = Input.InvalidInputException()
                response.statusCode = clientError.code
                response.end(getFailMessage(clientError))
            }
            is DateTimeParseException -> {
                val clientError = Input.InvalidInputException()
                response.statusCode = clientError.code
                response.end(getFailMessage(clientError))
            }
            is NullPointerException -> {
                val clientError = Input.InvalidInputException()
                response.statusCode = clientError.code
                response.end(getFailMessage(clientError))
            }
            else -> {
                response.statusCode = 500
                response.end(getFailMessage())
                logger.error(exception.stackTraceToString())
            }
        }
    }
}