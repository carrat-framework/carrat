package org.carrat.experimental

import kotlin.annotation.AnnotationTarget.*

@Retention(AnnotationRetention.SOURCE)
@RequiresOptIn(
    message = "This declaration is experimental and may be changed or removed in future."
)
@Target(
    CLASS,
    ANNOTATION_CLASS,
    TYPE_PARAMETER,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPE,
    TYPEALIAS
)
public annotation class CarratExperimental()
