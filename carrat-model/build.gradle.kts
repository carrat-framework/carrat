plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js(BOTH).browser()
    js(BOTH).useCommonJs()
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("serialization"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
                implementation(project(":carrat-experimental"))
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
