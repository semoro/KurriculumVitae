package me.semoro.kv.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable


object Skills : IntIdTable() {
    val name = text("name")
    val description = varchar("description", 1000)
    val level = integer("level")
    val cv = reference("cv", CVs)
}

object OpenSourceProjects : IntIdTable() {
    val name = text("name")
    val link = text("link")
    val description = varchar("description", 1000)
    val cv = reference("cv", CVs)
}

object AdditionalProjects : IntIdTable() {
    val name = text("name")
    val link = text("link")
    val description = varchar("description", 1000)
    val cv = reference("cv", CVs)
}


object CVs : IntIdTable() {
    val title = text("name")
    val biography = varchar("biography", 5000)
    val contactInfo = varchar("contactInfo", 1000)
}

object AccessTokens : IntIdTable() {

    val key = varchar("key", 16).uniqueIndex()
    val cv = reference("cv", CVs)
    val validThrough = date("validThrough")
    val views = integer("views")
}

val allTables = arrayOf(Skills, OpenSourceProjects, AdditionalProjects, CVs, AccessTokens)

class CV(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CV>(CVs)

    var title by CVs.title
    var biography by CVs.biography
    var contactInfo by CVs.contactInfo
    val skills by Skill referrersOn Skills.cv
    val openSourceProjects by OpenSourceProject referrersOn OpenSourceProjects.cv
    val additionalProjects by AdditionalProject referrersOn AdditionalProjects.cv
}


class AdditionalProject(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AdditionalProject>(AdditionalProjects)

    var name by AdditionalProjects.name
    var link by AdditionalProjects.link
    var description by AdditionalProjects.description
    var cv by CV referencedOn AdditionalProjects.cv
}

class OpenSourceProject(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OpenSourceProject>(OpenSourceProjects)

    var name by OpenSourceProjects.name
    var link by OpenSourceProjects.link
    var description by OpenSourceProjects.description
    var cv by CV referencedOn OpenSourceProjects.cv
}


class Skill(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Skill>(Skills)

    var name by Skills.name
    var description by Skills.description
    var level by Skills.level
    var cv by CV referencedOn Skills.cv
}


class AccessToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccessToken>(AccessTokens)

    var key by AccessTokens.key
    var validThrough by AccessTokens.validThrough
    var cv by CV referencedOn AccessTokens.cv
    var views by AccessTokens.views
}
