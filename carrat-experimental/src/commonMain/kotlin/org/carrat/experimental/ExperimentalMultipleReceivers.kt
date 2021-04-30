package org.carrat.experimental

@RequiresOptIn(
    message = "This declaration is experimental and will probably change when multiple receivers will be introduced in Kotlin."
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.TYPEALIAS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class ExperimentalMultipleReceivers(vararg val receivers :  String)
