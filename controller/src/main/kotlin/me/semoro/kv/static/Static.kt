package me.semoro.kv.static

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.resolveClasspathResource
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route



fun Route.staticFolder(base: String) {
    route(base) {
        get("*") {
            val resource = call.resolveClasspathResource(base, base)
            if (resource != null) {
                call.respond(resource)
            }
        }
    }
}