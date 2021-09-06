package org.carrat.build.compile

import com.sun.xml.xsom.XSSchema
import org.carrat.build.model.TagElementsMatches
import org.carrat.web.webapi.HTMLElement
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.KClass

val predefinedMatchesTagElement = mapOf(
    "h1" to "HTMLHeadingElement",
    "h2" to "HTMLHeadingElement",
    "h3" to "HTMLHeadingElement",
    "h4" to "HTMLHeadingElement",
    "h5" to "HTMLHeadingElement",
    "h6" to "HTMLHeadingElement",
    "p" to "HTMLParagraphElement",
    "img" to "HTMLImageElement",
    "col" to "HTMLTableColElement",
    "colgroup" to "HTMLTableColElement",
    "thead" to "HTMLTableSectionElement",
    "tfoot" to "HTMLTableSectionElement",
    "tbody" to "HTMLTableSectionElement",
    "caption" to "HTMLTableCaptionElement",
    "data" to "HTMLDataElement", //TODO: Missing
    "tr" to "HTMLTableRowElement",
    "template" to "HTMLTemplateElement", //TODO: Missing
    "dl" to "HTMLDListElement",
    "ins" to "HTMLModElement",
    "del" to "HTMLModElement",
    "a" to "HTMLAnchorElement",
    "slot" to "HTMLSlotElement", //TODO: Missing
    "ul" to "HTMLUListElement",
    "blockquote" to "HTMLQuoteElement",
    "q" to "HTMLQuoteElement",
    "cite" to "HTMLQuoteElement",
    "track" to "HTMLTrackElement", //TODO: Missing
    "th" to "HTMLTableCellElement",
    "td" to "HTMLTableCellElement",
    "ol" to "HTMLOListElement",
    //TODO: MathMLElement https://w3c.github.io/mathml-core/#dom-and-javascript
//    "command" to "HTMLCommandElement", //TODO: Missing in webapi
//    "keygen" to "HTMLKeygenElement", //TODO: Missing in webapi / deprecated
)
//TODO: Remove mathml

val deprecatedElementClasses = setOf(
    "HTMLFrameSetElement",
    "HTMLFrameElement",
    "HTMLFontElement",
    "HTMLMarqueeElement",
    "HTMLDirectoryElement",
)

val abstractElementClasses = setOf(
    "HTMLMediaElement"
)

val ignoredElementClasses = deprecatedElementClasses + abstractElementClasses + "HTMLUnknownElement"

@OptIn(ExperimentalStdlibApi::class)
fun buildTagElementsMatches(schema: XSSchema, elementClasses: Collection<KClass<*>>): TagElementsMatches {
    val tagNames = schema.collectTagNames()
    val elementClassesNames = collectElementClasses(elementClasses)
    val tagElement = mutableMapOf<String, String?>()

    tagNames.forEach { tagName ->
        val predefinedMatch = predefinedMatchesTagElement[tagName]
        if(predefinedMatch != null) {
            tagElement[tagName] = predefinedMatch
            elementClassesNames.remove(predefinedMatch.lowercase())
        } else {
            val elementClassName = elementClassesNames.remove("html${tagName}element")
            tagElement[tagName] = elementClassName
        }
    }

    return TagElementsMatches(
        tagElement,
        elementClassesNames.values.filter { it !in ignoredElementClasses }.toSet()
    )
}

@OptIn(ExperimentalStdlibApi::class)
private fun collectElementClasses(elementClasses: Collection<KClass<*>>): MutableMap<String, String> {
    return elementClasses.map { it.simpleName!! }.associateBy { it.lowercase() }
        .toMutableMap()
}

private fun XSSchema.collectTagNames(): Collection<String> {
    return this.elementDecls.keys
}