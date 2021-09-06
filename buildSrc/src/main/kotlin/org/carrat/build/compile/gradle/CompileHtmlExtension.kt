package org.carrat.build.compile.gradle

import org.gradle.api.provider.Property

abstract class CompileHtmlExtension {
    abstract fun getPackage(): Property<String>

    abstract fun getCommonOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    abstract fun getReportsDirectory(): Property<String> // https://github.com/gradle/gradle/issues/10690
}