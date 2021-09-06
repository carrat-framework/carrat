package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.model.Model

fun SourceSet.generateAnyTagConsumer(`package`: String, model: Model) {
    val className = "AnyTagConsumer"

    val extraConsumerInterfaces = model.tagGroups.map { "${it.value.typeName}Consumer" } +
            model.tags.map { "${it.value.className}Consumer" }
    file(`package`, "$className.kt") {
        `package`(`package`)
        appendLine()
        `interface`(
            setOf(Modifier.PUBLIC), "$className",
            extraConsumerInterfaces
        )
    }
}
