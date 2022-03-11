package server

import config.AppConfig
import controller.BaseController
import controller.FailHandler
import controller.admin.AdminHandler
import controller.balance.BalanceHandler
import controller.balance.SaveHandler
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
        registerRoute(router, SaveHandler(),"/save", HttpMethod.POST)
        registerRoute(router, BalanceHandler(),"/balance", HttpMethod.GET)
        registerRoute(router, AdminHandler(),"/admin/:cmd", HttpMethod.POST)
    }

    private fun registerRoute(router: Router, handler: BaseController<*>, path: String, method: HttpMethod){
        router.route(method, path)
            .handler(BodyHandler.create())
            .handler(handler)
            .failureHandler(FailHandler(handler::class.java))
    }
}