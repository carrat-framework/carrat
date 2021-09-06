package org.carrat.build.compile

import org.carrat.web.webapi.Element
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.KClass

@OptIn(ExperimentalStdlibApi::class)
fun discoverElementClasses(): Collection<KClass<out Element>> {
    val reflections = Reflections(webapiPackage, SubTypesScanner())
    return reflections.getSubTypesOf(Element::class.java).map { it.kotlin } + Element::class
}