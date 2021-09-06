plugins {
    kotlin("multiplatform")
    id("compileHtml")
}

configure<org.carrat.build.compile.gradle.CompileHtmlExtension> {
    getCommonOutput().set(buildDir.resolve("src/generatedSrc/html/commonMain").absolutePath)
    getPackage().set("org.carrat.web.builder.html")
    getReportsDirectory().set(buildDir.resolve("reports/html").absolutePath)
}

kotlin {
    jvm()
    js(BOTH).browser()
    js(BOTH).useCommonJs()
    sourceSets {
        commonMain {
            dependencies {
//                api(project(":carrat-web-fragments"))
                api(project(":carrat-model"))
                api(project(":carrat-context"))
                api("org.carrat:carrat-web-webapi")
                implementation(project(":carrat-experimental"))
                api("org.jetbrains.kotlin-wrappers:kotlin-css")
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
            }
        }
    }
    explicitApi()
}