package org.carrat.build

import java.util.regex.Pattern

val wellKnownWords = listOf(
    "span", "class", "enabled?", "edit(able)?",
    "^on", "encoded?", "form", "type",
    "run", "href", "drag(gable)?",
    "over", "mouse",
    "start(ed)?", "legend", "end(ed)?", "stop", "key", "load(ed)?", "check(ed)?",
    "time", "ready", "content", "changed?",
    "click", "play(ing)?", "context",
    "rows?", "cols?", "group(ed)?", "auto",
    "list", "field", "data", "block", "scripts?",
    "item", "area", "length", "colors?", "suspend", "focus", "touch",
    "caption", "foot(er)?", "brows(e|ing)", "cancel", "iteration", "error", "capture", "webkit", "animation",
    "down", "enter", "leave", "move", "pointer", "out", "up", "security", "policy", "violation",
    "waiting", "after", "print", "unload"
).map { it.toRegex(RegexOption.IGNORE_CASE) }

val excludeAttributes = listOf("^item$").map { Pattern.compile(it, Pattern.CASE_INSENSITIVE) }
fun isAttributeExcluded(name: String) = excludeAttributes.any { it.matcher(name).find() }

val excludedEnums = listOf("Lang$").map { it.toRegex(RegexOption.IGNORE_CASE) }
fun isEnumExcluded(name: String) = excludedEnums.any { it.containsMatchIn(name) }
