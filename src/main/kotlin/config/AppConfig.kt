package config

import common.Logger
import org.yaml.snakeyaml.Yaml

class AppConfig private constructor(){
    lateinit var server: ServerConfig
    lateinit var database: DatabaseConfig

    companion object {
        private val logger = Logger(AppConfig::class.java)
        private val yaml = Yaml()
        private var instance: AppConfig? = null

        fun loadConfig(): AppConfig {
            instance = yaml.loadAs(AppConfig::class.java.getResourceAsStream("/config/config.yaml"), AppConfig::class.java)
            logger.log("Config loaded.")
            return instance!!
        }

        fun getInstance(): AppConfig {
            if (instance == null)
                instance = loadConfig()
            return instance!!
        }
    }
}