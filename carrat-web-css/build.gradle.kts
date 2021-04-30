plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(BOTH).browser()
    js(BOTH).useCommonJs()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":carrat-web-fragments"))
                implementation(project(":carrat-web-builder"))
                api("org.jetbrains:kotlin-css")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
            }
        }
        js(BOTH).compilations["main"].defaultSourceSet {
            dependencies {
            }
        }
    }
    explicitApi()
}
