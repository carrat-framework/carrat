package org.carrat.build.compile.gradle

import org.carrat.build.compile.compile
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths

abstract class CompileHtmlTask : DefaultTask() {
    @Input
    abstract fun getPackage(): Property<String>

    @OutputDirectory
    abstract fun getCommonOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @OutputDirectory
    abstract fun getReportsDirectory(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @TaskAction
    fun compile() {
        compile(
            getPackage().get(),
            Paths.get(getCommonOutput().get()),
            Paths.get(getReportsDirectory().get())
        )
    }
}
