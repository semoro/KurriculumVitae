package me.semoro.kv.admin

import me.semoro.kv.model.allTables
import me.semoro.kv.utils.SystemConfiguration
import me.semoro.kv.view.View
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.route
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception


fun connectToDatabaseOrRedirectToRepair(call: ApplicationCall): Database {
    return SystemConfiguration.databaseConnectOrNull() ?: call.respondRedirect("/settings")
}

fun Route.installRoute() {
    route("/install") {
        post {
            if (!SystemConfiguration.isConfigured) {
                val adminPasswordHash = BCrypt.hashpw(it.parameters["adminPassword"]!!, BCrypt.gensalt())
                SystemConfiguration.config = SystemConfiguration.Config(
                        it.parameters["dbDriver"]!!,
                        it.parameters["dbConnectionString"]!!,
                        it.parameters["dbUser"]!!,
                        it.parameters["dbPassword"]!!,
                        it.parameters["externalUrl"]!!,
                        adminPasswordHash)

                try {
                    SystemConfiguration.connectToDatabase()
                    transaction {
                        create(*allTables)
                    }
                    SystemConfiguration.save()
                } catch (e: Exception) {
                    SystemConfiguration.config = null
                    call.respond(View.Install.createContent(
                            mapOf("dbConnectionString" to it.parameters["dbConnectionString"]!!,
                                    "dbUser" to it.parameters["dbUser"]!!,
                                    "dbPassword" to it.parameters["dbPassword"]!!,
                                    "externalUrl" to it.parameters["externalUrl"]!!,
                                    "dbError" to e.toString()
                            )))
                }

            }
            call.respondRedirect("/manage")
        }
        get {
            if (!SystemConfiguration.isConfigured)
                call.respond(View.Install.createContent(emptyMap()))
        }
    }
}

fun Route.settingsRoute() {
    route("/settings") {
        post {

        }
        get {
            if (!SystemConfiguration.isConfigured)
                call.respondRedirect("/install")
        }
    }
}


