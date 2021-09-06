package org.carrat.build.compile

import org.carrat.build.codegen.SourceSet
import org.carrat.build.codegen.appendIndentedLine
import org.carrat.build.codegen.file
import org.carrat.build.model.TagElementsMatches

fun SourceSet.generateTagElementMatchesReport(tagElementsMatches: TagElementsMatches) {
    file("tagElementMatchesReport.txt") {
        appendIndentedLine("Unmatched tags:")
        increaseIndent()
        tagElementsMatches.tagElement.filter { it.value == null }.map { it.key }.forEach {
            appendIndentedLine(it)
        }
        decreaseIndent()
        appendIndentedLine()
        appendIndentedLine("Unmatched element classes:")
        increaseIndent()
        tagElementsMatches.elementNoMatches.forEach {
            appendIndentedLine(it)
        }
        decreaseIndent()
        appendIndentedLine()
        appendIndentedLine("Matches:")
        tagElementsMatches.tagElement.forEach {
            appendIndentedLine("${it.key} : ${it.value}")
        }
    }
}