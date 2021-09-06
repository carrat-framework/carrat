package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.humanize
import org.carrat.build.model.Model
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers

fun SourceSet.generateElementBuilderExtensions(`package`: String, model: Model) {
    model.elementClasses.forEach { generateElementBuilderExtensions(`package`, it) }
}

fun SourceSet.generateElementBuilderExtensions(`package`: String, elementClass: KClass<*>) {
    val fileName = elementClass.simpleName!! + "BuilderExtensions"
    val eventExtensions = elementClass.declaredMembers.filterIsInstance<KProperty<*>>().filter { it.name.startsWith("on") }.map { prop ->
        val propReturnType = prop.returnType
        val arguments : List<String>
        val eventType : String?
        if(propReturnType.arguments.size == 2) {
            eventType = (propReturnType.arguments[0].type!!.classifier as KClass<*>).simpleName!!
            arguments = listOf(eventType)
        } else {
            eventType = null
            arguments = propReturnType.arguments.subList(0, propReturnType.arguments.size - 1).map {
                (it.type!!.classifier as KClass<*>).simpleName!! + if(it.type!!.isMarkedNullable) {"?"} else {""}
            }
        }
        val handlerReturnType = (propReturnType.arguments[1].type!!.classifier as KClass<*>).simpleName!!
        val returnType = if(handlerReturnType != "Any") {
            "$handlerReturnType?"
        } else {
            "Unit"
        }
        EventExtension(prop.name, eventType, arguments, returnType)
    }
    if(eventExtensions.isNotEmpty()) {
        val eventClasses = eventExtensions.map { it.eventClass }.filterNotNull().distinct().sorted()
        file(`package`, "$fileName.kt") {
            `package`(`package`)
            appendLine()
            import(elementClass.qualifiedName!!)
            eventClasses.forEach {
                import("$webapiPackage.$it")
            }
            appendLine()
            eventExtensions.forEach { ex ->
                    `fun`(
                        setOf(Modifier.PUBLIC),
                        "TagBuilder<*, ${elementClass.simpleName!!}>",
                        ex.name.humanize(),
                        listOf(
                            "handler : (${ex.arguments.joinToString(", ")})->${ex.returnType}"
                        ),
                        null
                    ) {
                        appendIndentedLine("attach { ${ex.name} = handler }")
                    }
                    appendLine()
                }
        }
    }
}

private class EventExtension(
    val name : String,
    val eventClass : String?,
    val arguments : List<String>,
    val returnType : String?
)
