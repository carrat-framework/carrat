package org.carrat.build.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun file(path: Path, block: IndentedAppendable.() -> Unit) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(
        path, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
    ).use{
        DefaultIndentedAppendable(it).block()
    }
}

fun file(path: Path, fileName: String, block: IndentedAppendable.() -> Unit) {
    file(path.resolve(fileName), block)
}

fun file(path: Path, `package`: String, fileName: String, block: IndentedAppendable.() -> Unit) {
    file(path.resolve(`package`.replace('.', '/')).resolve(fileName), block)
}