package org.carrat.experimental

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class ShouldUseTypeFunction(vararg val typeParameters : String)
