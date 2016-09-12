package me.semoro.kv.admin

import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route

fun Route.editRoute() {

}


fun Route.manageRoute() {
    route("/manage"){
        get {

        }
    }
}