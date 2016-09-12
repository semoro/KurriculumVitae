package me.semoro.kv.index

import me.semoro.kv.admin.getDatabaseConnectionOrRedirectToRepair
import me.semoro.kv.model.AccessTokens
import me.semoro.kv.model.Biographies
import me.semoro.kv.model.CVs
import me.semoro.kv.model.ContactInfo
import me.semoro.kv.utils.SystemConfiguration
import org.jetbrains.exposed.sql.select

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get


/**
 * Created by Semoro on 12.09.16.
 * ©XCodersTeam, 2016
 */


fun Route.indexRoute() {
    get {
        if (!SystemConfiguration.isConfigured) {
            call.respondRedirect("/install", permanent = false)
        }

        call.request.queryParameters["token"]?.let { token ->
            getDatabaseConnectionOrRedirectToRepair(call)
            (AccessTokens innerJoin CVs innerJoin Biographies innerJoin ContactInfo).select { AccessTokens.key eq token }
                    .forEach {
                        println(it.data)
                    }
        }

    }
}