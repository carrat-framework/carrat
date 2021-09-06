package org.carrat.build.codegen

class Setter(
    val modifiers : Set<Modifier>,
    val functionValueParameterWithOptionalType : String,
    val type :  String?,
    val body : FunctionBody?
)