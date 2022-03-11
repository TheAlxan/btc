package controller

import com.google.gson.annotations.SerializedName
import common.JsonParser
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

abstract class BaseHandler<T>(private val clazz: Class<T>): Handler<RoutingContext> {
    private val errorMessage = "Something went wrong!"

    fun deserialize(req: RoutingContext) = JsonParser.parseTo(req.bodyAsString ?: "", clazz)
    fun getPathParameter(context: RoutingContext, name: String): String? = context.request().getParam(name)

    data class Request<T>(val context: RoutingContext, val input: T)
    data class Response<T>(val result: T, val statusCode: Int) {
        fun serialize() = JsonParser.serialize(result!!)

        companion object {
            fun fromDefaultSuccess(): Response<ResponseMessage> {
                return Response(ResponseMessage("Successful!"), 200)
            }

            fun <T> from(result: T): Response<T> {
                return Response(result, 200)
            }
        }
    }
    class ResponseMessage(@SerializedName("message") val message: String)

    fun getFailMessage(exception: Exception): String {
        val response = ResponseMessage(exception.message ?: errorMessage)
        return JsonParser.serialize(response) ?: errorMessage
    }

    fun getFailMessage(): String {
        val response = ResponseMessage(errorMessage)
        return JsonParser.serialize(response) ?: errorMessage
    }
}