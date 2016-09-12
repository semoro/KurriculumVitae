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


/**
 * Created by Semoro on 12.09.16.
 * ©XCodersTeam, 2016
 */


fun getDatabaseConnectionOrRedirectToRepair(call: ApplicationCall): Database {
    return SystemConfiguration.databaseConnectOrNull() ?: call.respondRedirect("/settings")
}

fun Route.installRoute() {
    route("/install") {
        post {
            if (!SystemConfiguration.isConfigured) {
                val adminPasswordHash = BCrypt.hashpw(it.parameters["adminPassword"]!!, BCrypt.gensalt())
                SystemConfiguration.config = SystemConfiguration.Config(it.parameters["dbDriver"]!!, it.parameters["dbConnectionString"]!!, adminPasswordHash)
                val db = getDatabaseConnectionOrRedirectToRepair(call)
                transaction {
                    create(*allTables)
                }
                SystemConfiguration.save()
            }
            call.respondRedirect("/edit")
        }
        get {
            if (!SystemConfiguration.isConfigured)
                call.respond(View.Install.createContent(emptyMap()))
        }
    }
}


