package org.carrat.build.compile

import org.carrat.build.codegen.SourceSet
import org.carrat.build.compile.*
import org.carrat.build.parseDocInfos
import java.nio.file.Path

fun compile(`package`: String, sourceSetPath: Path, reportsDirectory: Path) {
    val schema = parse()
    val docInfos = parseDocInfos()
    val elementClasses = discoverElementClasses()
    val tagElementsMatches = buildTagElementsMatches(schema, elementClasses)
    val reportsSourceSet = SourceSet(reportsDirectory)
    reportsSourceSet.generateTagElementMatchesReport(tagElementsMatches)
    val context = buildContext(docInfos)
    val model = context.buildModel(schema, tagElementsMatches, elementClasses)
    val sourceSet = SourceSet(sourceSetPath)
    sourceSet.generateTags(`package`, model)
    sourceSet.generateTypes(`package`, model)
    sourceSet.generateTagGroups(`package`, model)
    sourceSet.generateAttributeGroups(`package`, model)
    sourceSet.generateAttributeTypes(`package`, model)
    sourceSet.generateAnyTagConsumer(`package`, model)
    sourceSet.generateElementBuilderExtensions(`package`, model)
}