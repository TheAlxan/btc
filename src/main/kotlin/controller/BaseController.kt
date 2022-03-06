package controller

import common.JsonParser
import dto.BaseDto
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

abstract class BaseController<T: BaseDto>(private val clazz: Class<T>): Handler<RoutingContext> {
    abstract fun guard(input: T)
    abstract fun handleRequest(input: T)
    override fun handle(req: RoutingContext) {
        val input: T = deserialize(req)
        guard(input)
        handleRequest(input)
    }

    private fun deserialize(req: RoutingContext) = JsonParser.parseTo(req.bodyAsString, clazz)
}