package org.carrat.experimental

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@RequiresOptIn(
    message = "Interface may be unsealed in future, once API stabilizes."
)
public annotation class SealedApi
