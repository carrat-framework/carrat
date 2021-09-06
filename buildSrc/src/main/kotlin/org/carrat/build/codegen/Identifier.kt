package org.carrat.build.codegen

private val keywords = setOf(
	"as",
	"break",
	"class",
	"continue",
	"do",
	"else",
	"false",
	"for",
	"fun",
	"if",
	"in",
	"interface",
	"is",
	"null",
	"object",
	"package",
	"return",
	"super",
	"this",
	"throw",
	"true",
	"try",
	"typealias",
	"typeof",
	"val",
	"var",
	"when",
	"while"
)

data class Identifier(val identifier: String) {
    val requiresEscape : Boolean
        get() = identifier in keywords

    override fun toString(): String = if(requiresEscape) {
		"`$identifier`"
	} else {
		identifier
	}
}