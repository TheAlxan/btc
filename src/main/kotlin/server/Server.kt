package server

import config.AppConfig
import controller.BalanceHandler
import controller.SaveHandler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class Server {
    private val vertx: Vertx = Vertx.vertx()
    fun createServer() {
        val server = vertx.createHttpServer()
        server.requestHandler(getRouter())
        server.listen(AppConfig.getInstance().server.port!!, AppConfig.getInstance().server.ip!!)
    }

    private fun getRouter(): Router? {
        val router = Router.router(vertx)
        setUpRoutes(router)
        return router
    }

    private fun setUpRoutes(router: Router) {
        router.route(HttpMethod.POST, "/save")
            .handler(BodyHandler.create())
            .handler(SaveHandler())

        router.route(HttpMethod.GET, "/balance")
            .handler(BodyHandler.create())
            .handler(BalanceHandler())
    }
}