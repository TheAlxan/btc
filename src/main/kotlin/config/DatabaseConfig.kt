package config

class DatabaseConfig: BaseConfig() {
    var username: String? = null
    var password: String? = null
    var url: String? = null
    var fixDelay: Long = 10L
}