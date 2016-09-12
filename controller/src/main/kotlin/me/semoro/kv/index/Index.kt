package me.semoro.kv.index

import me.semoro.kv.admin.connectToDatabaseOrRedirectToRepair
import me.semoro.kv.admin.processImmediately
import me.semoro.kv.model.AccessToken
import me.semoro.kv.model.AccessTokens
import me.semoro.kv.utils.SystemConfiguration
import me.semoro.kv.view.View
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import java.text.SimpleDateFormat

val standardDateFormat = SimpleDateFormat("yyyy-MM-dd")

fun Route.indexRoute() {
    get("{token}") {
        connectToDatabaseOrRedirectToRepair(call)
        if (!SystemConfiguration.isConfigured) {
            call.respondRedirect("/install")
        }

        call.parameters["token"]?.let { token ->
            transaction {
                val foundToken = AccessToken.find { AccessTokens.key eq token }.firstOrNull()!!
                if (foundToken.validThrough.isAfterNow) {
                    foundToken.views = foundToken.views + 1
                    commit()
                    call.respondText(ContentType.Text.Html, View.Index.createContent(mapOf("token" to foundToken)).processImmediately())
                }
            }
        }
    }
}