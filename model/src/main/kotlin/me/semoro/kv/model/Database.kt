package me.semoro.kv.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable



object Skills : IntIdTable() {
    val parentId = reference("parentId", id).nullable()
    val name = text("name")
    val description = varchar("description", 1000)
    val level = integer("level")
}

object OpenSourceProjects : IntIdTable() {
    val name = text("name")
    val link = text("link")
    val description = varchar("description", 1000)
    val cv = reference("cv", CVs)
}

object AdditionalProjects : IntIdTable() {
    val name = text("name")
    val file = reference("file", Uploads)
    val description = varchar("description", 1000)
    val cv = reference("cv", CVs)
}


object Uploads : IntIdTable() {

}

object CVs : IntIdTable() {
    val title = text("name")
    val biography = varchar("biography", 5000)
    val contactInfo = varchar("contactInfo", 1000)
    val rootSkill = reference("rootSkill", Skills)
}

object AccessTokens : IntIdTable() {

    val key = varchar("key", 16).uniqueIndex()
    val cv = reference("cv", CVs)
    val validThrough = date("validThrough")
}

val allTables = arrayOf(Skills, OpenSourceProjects, AdditionalProjects, Uploads, CVs, AccessTokens)

class CV(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CV>(CVs)

    var title by CVs.title
    var biography by CVs.biography
    var contactInfo by CVs.contactInfo
    var rootSkill by Skill referencedOn CVs.rootSkill
    val openSourceProjects by OpenSourceProject referrersOn OpenSourceProjects.cv
    val additionalProjects by AdditionalProject referrersOn AdditionalProjects.cv
}


class AdditionalProject(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AdditionalProject>(AdditionalProjects)

    var name by AdditionalProjects.name
    var file by Upload referencedOn AdditionalProjects.file
    var description by AdditionalProjects.description
}

class OpenSourceProject(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OpenSourceProject>(OpenSourceProjects)

    var name by OpenSourceProjects.name
    var link by OpenSourceProjects.link
    var description by OpenSourceProjects.description
}


class Skill(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Skill>(Skills)

    var parent by Skill optionalReferencedOn Skills.parentId
    var name by Skills.name
    var description by Skills.description
    var level by Skills.level
}


class Upload(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Upload>(Uploads)

}

class AccessToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccessToken>(AccessTokens)

    var key by AccessTokens.key
    var validThrough by AccessTokens.validThrough
    var cv by CV referencedOn AccessTokens.cv

}
