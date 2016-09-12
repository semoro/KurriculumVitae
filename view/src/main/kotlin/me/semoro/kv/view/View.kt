package me.semoro.kv.view

import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.template.ClasspathTemplateLoader
import me.semoro.ktor.jade.JadeContent

/**
 * Created by Semoro on 12.09.16.
 * ©XCodersTeam, 2016
 */
object ViewConfig {
    val jadeConfig = JadeConfiguration()

    init {
        val loader = ClasspathTemplateLoader()
        jadeConfig.templateLoader = loader
    }

}


enum class View(_templateName: String) {
    Install("install"),
    Index("index");

    val templateName = "templates/$_templateName.jade"
    fun createContent(model: Map<String, Any>): JadeContent {
        return JadeContent(templateName, model, "")
    }
}