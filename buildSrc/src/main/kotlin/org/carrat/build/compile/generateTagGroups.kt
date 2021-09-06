package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.humanize
import org.carrat.build.model.Model
import org.carrat.build.model.TagGroup

fun SourceSet.generateTagGroups(`package`: String, model: Model) {
    model.tagGroups.values.forEach { generateTagGroup(`package`, it) }
}

fun SourceSet.generateTagGroup(`package`: String, tagGroup: TagGroup) {
    val className = tagGroup.consumerTypeName
    val extraConsumerInterfaces = tagGroup.elements.map { "${it.humanize().capitalize()}Consumer" }
    file(`package`, "$className.kt") {
        `package`(`package`)
        appendLine()
        `interface`(
            setOf(Modifier.PUBLIC), className,
            extraConsumerInterfaces
        )
    }
}
