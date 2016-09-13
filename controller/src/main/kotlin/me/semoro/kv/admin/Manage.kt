package me.semoro.kv.admin

import com.google.gson.JsonParser
import me.semoro.ktor.jade.JadeContent
import me.semoro.kv.index.standardDateFormat
import me.semoro.kv.model.*
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
        post("{id}") {
            connectToDatabaseOrRedirectToRepair(call)
            transaction {
                val cv = CV.findById(it.parameters["id"]!!.toInt())

                if (cv != null) {
                    val content = call.request.content.get<String>()
                    val json = JsonParser().parse(content).asJsonObject
                    val biography = json.get("biography").asString
                    val contactInfo = json.get("contactInfo").asString
                    val title = json.get("title").asString

                    cv.biography = biography
                    cv.contactInfo = contactInfo
                    cv.title = title

                    val skills = json.get("skills").asJsonArray.map { it.asJsonObject }
                    val skillIds = mutableListOf<Int>()
                    skills.filter { it.has("id") }.forEach {
                        jsonSkill ->
                        skillIds.add(jsonSkill["id"].asInt)
                        val skill = cv.skills.filter { it.id.value == jsonSkill["id"].asInt }.first()
                        skill.name = jsonSkill["name"].asString
                        skill.description = jsonSkill["description"].asString
                        skill.level = jsonSkill["level"].asInt
                    }
                    cv.skills.filterNot { skillIds.contains(it.id.value) }.forEach(Skill::delete)
                    skills.filterNot { it.has("id") }.forEach {
                        jsonSkill ->
                        Skill.new {
                            name = jsonSkill["name"].asString
                            description = jsonSkill["description"].asString
                            level = jsonSkill["level"].asInt
                            this.cv = cv
                        }
                    }

                    /////////////////
                    val openSourceProjects = json.get("openSourceProjects").asJsonArray.map { it.asJsonObject }
                    val openSourceProjectIds = mutableListOf<Int>()
                    openSourceProjects.filter { it.has("id") }.forEach {
                        jsonOpenSourceProject ->
                        openSourceProjectIds.add(jsonOpenSourceProject["id"].asInt)
                        val openSourceProject = cv.openSourceProjects.filter { it.id.value == jsonOpenSourceProject["id"].asInt }.first()
                        openSourceProject.name = jsonOpenSourceProject["name"].asString
                        openSourceProject.description = jsonOpenSourceProject["description"].asString
                        openSourceProject.link = jsonOpenSourceProject["link"].asString
                    }
                    cv.openSourceProjects.filterNot { openSourceProjectIds.contains(it.id.value) }.forEach(OpenSourceProject::delete)
                    openSourceProjects.filterNot { it.has("id") }.forEach {
                        jsonOpenSourceProject ->
                        OpenSourceProject.new {
                            name = jsonOpenSourceProject["name"].asString
                            description = jsonOpenSourceProject["description"].asString
                            link = jsonOpenSourceProject["link"].asString
                            this.cv = cv
                        }
                    }
                    /////////////


                    /////////////////
                    val additionalProjects = json.get("additionalProjects").asJsonArray.map { it.asJsonObject }
                    val additionalProjectIds = mutableListOf<Int>()
                    additionalProjects.filter { it.has("id") }.forEach {
                        jsonAdditionalProject ->
                        additionalProjectIds.add(jsonAdditionalProject["id"].asInt)
                        val additionalProject = cv.additionalProjects.filter { it.id.value == jsonAdditionalProject["id"].asInt }.first()
                        additionalProject.name = jsonAdditionalProject["name"].asString
                        additionalProject.description = jsonAdditionalProject["description"].asString
                        additionalProject.link = jsonAdditionalProject["link"].asString
                    }
                    cv.additionalProjects.filterNot { additionalProjectIds.contains(it.id.value) }.forEach(AdditionalProject::delete)
                    additionalProjects.filterNot { it.has("id") }.forEach {
                        jsonAdditionalProject ->
                        AdditionalProject.new {
                            name = jsonAdditionalProject["name"].asString
                            description = jsonAdditionalProject["description"].asString
                            link = jsonAdditionalProject["link"].asString
                            this.cv = cv
                        }
                    }
                    /////////////

                    commit()
                    call.respondText(ContentType.Application.Json, "\"ok\"")
                }
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
                CV.new {
                    title = call.parameters["title"]!!
                    biography = ""
                    contactInfo = ""
                }
            }
            call.respondRedirect("/edit/${cv.id}")
        }
    }
}