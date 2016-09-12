package me.semoro.kv

import me.semoro.ktor.jade.jadeTemplate
import me.semoro.kv.admin.editRoute

import me.semoro.kv.admin.installRoute

import me.semoro.kv.index.indexRoute
import me.semoro.kv.utils.SystemConfiguration
import me.semoro.kv.view.ViewConfig
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationEnvironment
import org.jetbrains.ktor.content.templating
import org.jetbrains.ktor.features.http.ConditionalHeadersSupport
import org.jetbrains.ktor.features.http.DefaultHeaders
import org.jetbrains.ktor.features.http.PartialContentSupport
import org.jetbrains.ktor.features.install
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.util.concurrent.TimeUnit

/**
 * Created by Semoro on 11.09.16.
 * ©XCodersTeam, 2016
 */
class KVApplication(environment: ApplicationEnvironment) : Application(environment) {
    init {
        install(DefaultHeaders)
        install(CallLogging)
        install(ConditionalHeadersSupport)
        install(PartialContentSupport)
        install(Locations)
        executor.schedule({
            templating(jadeTemplate { ViewConfig.jadeConfig })
        }, 100, TimeUnit.MILLISECONDS) //Hack(


        routing {
            indexRoute()
            installRoute()
            editRoute()

        }
    }
}