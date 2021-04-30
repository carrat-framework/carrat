plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(BOTH) {
        browser {
            commonWebpackConfig {
                sourceMaps = true
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-html")
                api("org.carrat:carrat-web-webapi")
                implementation(project(":carrat-experimental"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
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
//                implementation(npm("history", "4.10.1"))
            }
        }
    }
    explicitApi()
}
