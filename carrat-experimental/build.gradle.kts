plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(BOTH).browser()
    js(BOTH).useCommonJs()
    explicitApi()
}
