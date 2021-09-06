package org.carrat.build.codegen

class Getter(
    val modifiers: Set<Modifier>,
    val type: String?,
    val body: FunctionBody?
)