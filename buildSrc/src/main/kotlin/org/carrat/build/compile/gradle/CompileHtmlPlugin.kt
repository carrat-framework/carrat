package org.carrat.build.compile.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

class CompileHtmlPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("compileHtml", CompileHtmlExtension::class.java)

        val compileHtmlTask = project.tasks.register("compileHtml", CompileHtmlTask::class.java) {
            it.getCommonOutput().set(extension.getCommonOutput())
            it.getPackage().set(extension.getPackage())
            it.getReportsDirectory().set(extension.getReportsDirectory())
        }

        project.afterEvaluate {
            addOutputDirectory(project, "commonMain", extension.getCommonOutput().get(), compileHtmlTask, extension)
            project.tasks.withType(AbstractKotlinCompileTool::class.java).forEach {
                it.dependsOn(compileHtmlTask)
            }
        }
    }

    private fun addOutputDirectory(
        project: Project,
        sourceSet: String,
        path: String,
        compileWebIdlTask: TaskProvider<*>,
        extension: CompileHtmlExtension
    ) {
        project.extensions.configure(KotlinMultiplatformExtension::class.java) {
            val kotlin = it.sourceSets.named(sourceSet).get().kotlin
            kotlin.srcDir(path)
            kotlin.compiledBy(compileWebIdlTask) {
                val directoryProperty = project.objects.directoryProperty()
                directoryProperty.set(directoryProperty.dir(extension.getCommonOutput()))
                directoryProperty
            }
        }
    }
}