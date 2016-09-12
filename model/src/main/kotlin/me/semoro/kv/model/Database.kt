package me.semoro.kv.model

import org.jetbrains.exposed.sql.Table

/**
 * Created by Semoro on 12.09.16.
 * ©XCodersTeam, 2016
 */


object Skills : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val parentId = reference("parentId", id).nullable()
    val name = text("name")
    val description = varchar("description", 1000)
    val level = integer("level")
    val cv = reference("cv", CVs.id)
}

object ContactInfo : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val text = varchar("description", 1000)
}

object OpenSourceProjects : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = text("name")
    val link = text("link")
    val description = varchar("description", 1000)
    val cv = reference("cv", CVs.id)
}

object AdditionalProjects : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = text("name")
    val file = reference("file", Uploads.id)
    val description = varchar("description", 1000)
    val cv = reference("cv", CVs.id)
}

object Biographies : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val text = varchar("description", 5000)
}

object Uploads : Table() {
    val id = integer("id").autoIncrement().primaryKey()
}

object CVs : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val biography = reference("biography", Biographies.id)
    val contactInfo = reference("contactInfo", ContactInfo.id)
}

object AccessTokens : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val key = varchar("key", 16).uniqueIndex()
    val cv = reference("cv", CVs.id)
    val validThrough = date("validThrough")
}

val allTables = arrayOf(Skills, ContactInfo, OpenSourceProjects, AdditionalProjects, Biographies, Uploads, CVs, AccessTokens)