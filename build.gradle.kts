import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    val kotlinVersion by extra("1.5.0")

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}

val kotlinVersion = extra["kotlinVersion"]

plugins {
    kotlin("plugin.serialization") version "1.5.0" apply false
    kotlin("multiplatform") version "1.5.0" apply false
}

allprojects {
    group = "org.carrat"
    version("0.0alpha0")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://kotlin.bintray.com/kotlin-js-wrappers")
        maven("https://kotlin.bintray.com/kotlin-eap")
        maven("https://kotlin.bintray.com/kotlin-dev")
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/carrat/carrat")
        maven("https://carrat.jfrog.io/artifactory/carrat-dev")
    }
}

subprojects {
    apply(plugin = "maven-publish")

    val kotlinVersion by extra("1.5.0")
    val kotlinWrappersVersion by extra("pre.134-kotlin-1.4.21")
    val kotlinxSerializationVersion by extra("1.0.1")

    val recommendedVersion by configurations.creating {
        isCanBeResolved = false
        isCanBeConsumed = false
    }
    configurations.all {
        if (isCanBeResolved) {
            extendsFrom(recommendedVersion)
        }
    }
    if(path != ":carrat-bom") {
        dependencies {
            platform(kotlin("bom"))
            constraints {
                recommendedVersion("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
                recommendedVersion("org.jetbrains:kotlin-css:1.0.0-$kotlinWrappersVersion")
                recommendedVersion("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
//                recommendedVersion("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.1")
                recommendedVersion("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
                recommendedVersion("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.3")
                recommendedVersion("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.3")
//                recommendedVersion("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.1")
                recommendedVersion("org.carrat:carrat-web-webapi:0.0alpha0.0preview0")
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    afterEvaluate {
        if (plugins.hasPlugin("kotlin-multiplatform")) {
            configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                sourceSets {
                    all {
                        languageSettings.languageVersion = "1.5"
                        languageSettings.apiVersion = "1.5"
                        languageSettings.useExperimentalAnnotation("org.carrat.experimental.ExperimentalMultipleReceivers")
                        languageSettings.useExperimentalAnnotation("org.carrat.experimental.CarratExperimental")
                        languageSettings.useExperimentalAnnotation("org.carrat.experimental.LowerBound")
                        languageSettings.useExperimentalAnnotation("org.carrat.experimental.SealedApi")
                        languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
                    }
                }
            }
        }
    }

    configure<PublishingExtension> {
        publications {
            forEach {
                if(it is MavenPublication) {
                    it.pom {
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "carrat"
                url = uri("https://carrat.jfrog.io/artifactory/carrat-dev/")
                credentials(PasswordCredentials::class)
            }
        }
    }

}
