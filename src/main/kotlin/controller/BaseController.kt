package controller

import common.JsonParser
import dto.BaseDto
import exception.BaseException
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

abstract class BaseController<T: BaseDto>(private val clazz: Class<T>): Handler<RoutingContext> {
    abstract fun guard(input: T)
    abstract fun handleRequest(input: T): Any?
    override fun handle(req: RoutingContext) {
        try {
            val input: T = deserialize(req)
            guard(input)
            val result = handleRequest(input)?.let { JsonParser.serialize(it) } ?: "Successful!"
            req.end(result)
        } catch (e: Exception) {
            if (e is BaseException) {
                req.end(e.msg)
            } else {
                req.end("Something went wrong!")
                e.printStackTrace()
            }
        }
    }

    private fun deserialize(req: RoutingContext) = JsonParser.parseTo(req.bodyAsString, clazz)
}