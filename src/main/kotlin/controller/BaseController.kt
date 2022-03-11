package controller

import common.Logger
import io.vertx.ext.web.RoutingContext
import java.util.*

abstract class BaseController<T>(clazz: Class<T>): BaseHandler<T>(clazz) {
    private val logger = Logger(clazz)
    abstract fun guard(request: Request<T>)
    abstract fun handleRequest(request: Request<T>): Any?
    override fun handle(ctx: RoutingContext) {
        logger.log("Request from ${ctx.request().remoteAddress()} at ${Date()}")
        val request = Request<T>(ctx, deserialize(ctx))
        guard(request)
        val result = handleRequest(request)?.let { Response.from(it) } ?: Response.fromDefaultSuccess()
        ctx.response().putHeader("Content-Type", "application/json")
        ctx.end(result.serialize())
    }
}