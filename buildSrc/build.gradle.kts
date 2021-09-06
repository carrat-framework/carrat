plugins {
    kotlin("jvm") version "1.4.32"
    id("java-gradle-plugin")
}

gradlePlugin {
    plugins {
        create("compileHtml") {
            id = "compileHtml"
            implementationClass = "org.carrat.build.compile.gradle.CompileHtmlPlugin"
        }
    }
}

kotlin {
    sourceSets.all {
        languageSettings.enableLanguageFeature("AllowSealedInheritorsInDifferentFilesOfSamePackage")
        languageSettings.enableLanguageFeature("SealedInterfaces")
    }
}

repositories {
    mavenCentral()
    maven("https://tomaszrocks.jfrog.io/artifactory/carrat-dev/")
}

dependencies {
    implementation(gradleApi())
    implementation("com.sun.xsom:xsom:20140925")
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.carrat:carrat-web-webapi:0.0alpha0.0preview0")
    implementation(kotlin("gradle-plugin", version = "1.5.21"))
}
