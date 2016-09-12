package me.semoro.kv.admin

import me.semoro.ktor.jade.JadeContent
import me.semoro.kv.index.standardDateFormat
import me.semoro.kv.model.AccessToken
import me.semoro.kv.model.CV
import me.semoro.kv.model.Skill
import me.semoro.kv.utils.SystemConfiguration
import me.semoro.kv.view.View
import me.semoro.kv.view.ViewConfig
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.route
import org.joda.time.DateTime
import java.util.*

fun Route.editRoute() {
    route("/edit") {
        get("{id}") {
            connectToDatabaseOrRedirectToRepair(call)
            transaction {
                val cv = CV.findById(it.parameters["id"]!!.toInt())
                if (cv != null)
                    call.respondText(ContentType.Text.Html, View.Edit.createContent(mapOf("CV" to cv)).processImmediately())
            }
        }
    }
}

fun JadeContent.processImmediately(): String
        = ViewConfig.jadeConfig.renderTemplate(ViewConfig.jadeConfig.getTemplate(templateName), model)

val symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
fun randomString(len: Int): String {
    val sb = StringBuilder()
    Random().ints(len.toLong(), 0, symbols.length).forEach { sb.append(symbols[it]) }
    return sb.toString()
}

fun Route.manageRoute() {
    route("/manage") {
        get {
            connectToDatabaseOrRedirectToRepair(call)
            transaction {
                val tokens = AccessToken.all()
                val allCV = CV.all()
                call.respondText(ContentType.Text.Html, View.Manage.createContent(mapOf("tokens" to tokens, "allCV" to allCV, "externalUrl" to SystemConfiguration.config!!.externalSiteUrl, "standardDateFormat" to standardDateFormat)).processImmediately())
            }
        }
        get("revokeToken/{id}") {
            connectToDatabaseOrRedirectToRepair(call)
            transaction {
                AccessToken.findById(it.parameters["id"]!!.toInt())?.delete()
            }
            call.respondRedirect("/manage")
        }
        post("createToken") {
            connectToDatabaseOrRedirectToRepair(call)
            transaction {
                AccessToken.new {
                    cv = CV.findById(it.parameters["cvId"]!!.toInt())!!
                    validThrough = DateTime(standardDateFormat.parse(it.parameters["validThrough"]))
                    key = randomString(16)
                    views = 0
                }
            }
            call.respondRedirect("/manage")
        }
        post("createCV") {
            connectToDatabaseOrRedirectToRepair(call)
            val cv = transaction {
                val skill = Skill.new {
                    parent = null
                    name = ""
                    description = ""
                    level = 10
                }
                CV.new {
                    title = call.parameters["title"]!!
                    biography = ""
                    contactInfo = ""
                    rootSkill = skill
                }
            }
            call.respondRedirect("/edit/${cv.id}")
        }
    }
}