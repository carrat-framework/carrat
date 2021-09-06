package org.carrat.build.codegen

import java.nio.file.Path

class SourceSet(val path : Path)


fun SourceSet.file(fileName : String, block : IndentedAppendable.() -> Unit) {
    file(path, fileName, block)
}

fun SourceSet.file(`package` : String, fileName : String, block : IndentedAppendable.() -> Unit) {
    file(path, `package`, fileName, block)
}