package me.semoro.kv.utils


import com.google.gson.Gson
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception


object SystemConfiguration {
    val configFile = File("systemConfig.json")

    data class Config(var dbDriver: String,
                      var dbConnectionString: String,
                      var dbUser: String = "",
                      var dbPassword: String = "",
                      var externalSiteUrl: String,
                      var adminPasswordHash: String)

    var config: Config?


    fun connectToDatabase(): Database {
        return Database.connect(config!!.dbConnectionString, config!!.dbDriver, config!!.dbUser, config!!.dbPassword)
    }

    fun databaseConnectOrNull(): Database? {
        if (config != null) {
            try {
                return connectToDatabase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    init {
        if (configFile.exists()) {
            config = Gson().fromJson(FileReader(configFile), Config::class.java)
        } else
            config = null
    }

    fun save() {
        val writer = FileWriter(configFile)
        Gson().toJson(config, writer)
        writer.close()
    }

    val isConfigured: Boolean
        get() = config != null

}