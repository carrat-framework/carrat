package org.carrat.build.compile

import com.sun.xml.xsom.XSSchema
import com.sun.xml.xsom.parser.XSOMParser
import org.carrat.build.asResourceUrl
import javax.xml.parsers.SAXParserFactory

val SCHEME_URL = "html_5.xsd".asResourceUrl()
const val HTML_NAMESPACE = "html-5"

fun parse(): XSSchema {
    val parser = XSOMParser(SAXParserFactory.newInstance())
    parser.parse(SCHEME_URL)
    return parser.result.getSchema(HTML_NAMESPACE)
}