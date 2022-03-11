import config.AppConfig
import data.DatabaseConnector
import data.DatabaseMaintainer
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import server.Server

object App {
    fun startApplication(args: Array<String>) {
        AppConfig.loadConfig()
        parseArgs(args)
        DatabaseConnector.connect()
        DatabaseMaintainer.initialize()
        Server().createServer()
    }

    private fun parseArgs(args: Array<String>){
        val options = Options()
            .addOption(Option("ip", true, "Ip address of the server"))
            .addOption(Option("port", true, "Port number of the server"))

        val parser = DefaultParser()
        val config = parser.parse(options, args)
        if (config.hasOption("ip"))
            AppConfig.getInstance().server.ip = config.getOptionValue("ip")
        if (config.hasOption("port"))
            AppConfig.getInstance().server.port = config.getOptionValue("port").toInt()
    }
}