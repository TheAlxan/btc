package controller

import common.JsonParser
import dto.BaseDto
import exception.BaseException
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

abstract class BaseController<T>(private val clazz: Class<T>): Handler<RoutingContext> {
    abstract fun guard(request: Request<T>)
    abstract fun handleRequest(request: Request<T>): Any?
    override fun handle(ctx: RoutingContext) {
        try {
            val request = Request<T>(ctx, deserialize(ctx))
            guard(request)
            val result = handleRequest(request)?.let { JsonParser.serialize(it) } ?: "Successful!"
            ctx.end(result)
        } catch (e: Exception) {
            if (e is BaseException) {
                ctx.end(e.msg)
            } else {
                ctx.end("Something went wrong!")
                e.printStackTrace()
            }
        }
    }

    private fun deserialize(req: RoutingContext) = JsonParser.parseTo(req.bodyAsString ?: "", clazz)
    fun getPathParameter(context: RoutingContext, name: String): String? = context.request().getParam(name)

    data class Request<T>(val context: RoutingContext, val input: T)
}