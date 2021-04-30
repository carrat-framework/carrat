package org.carrat.experimental

@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@RequiresOptIn(
    message = "Declaration will change if type parameters lower bounds will be introduced to kotlin type system."
)
public annotation class LowerBound(val value: String)
