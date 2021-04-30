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
                api(project(":carrat-experimental"))
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
