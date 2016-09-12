package me.semoro.kv.index

import me.semoro.kv.admin.connectToDatabaseOrRedirectToRepair
import me.semoro.kv.model.AccessToken
import me.semoro.kv.model.AccessTokens
import me.semoro.kv.utils.SystemConfiguration
import me.semoro.kv.view.View
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get





fun Route.indexRoute() {
    get {
        if (!SystemConfiguration.isConfigured) {
            call.respondRedirect("/install", permanent = false)
        }

        call.request.queryParameters["token"]?.let { token ->
            connectToDatabaseOrRedirectToRepair(call)
            val foundToken = AccessToken.find { AccessTokens.key eq token }.firstOrNull()!!
            call.respond(View.Index.createContent(mapOf("access" to foundToken)))
        }
    }
}