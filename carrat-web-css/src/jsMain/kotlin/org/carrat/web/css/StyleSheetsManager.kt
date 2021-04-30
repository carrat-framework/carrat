package org.carrat.web.css

import org.carrat.experimental.CarratExperimental
import org.carrat.web.webapi.*

@CarratExperimental
internal actual class StyleSheetsManager {
    private var injectedStylesElement: Element? = null
    private val importedStyleSheets by lazy {
        val names = mutableSetOf<String>()
        val urls = mutableSetOf<String>()
        for(styleSheet in document.styleSheets.asList()){
            when(val node = styleSheet.ownerNode) {
                is HTMLStyleElement -> {
                    for(rule in styleSheet.cssRules.asList()){
                        if(rule is CSSImportRule) {
                            urls += rule.href
                        }
                    }
                }
                is HTMLLinkElement -> {
                    names += node.href
                }
            }
        }
        object {
            val names = names
            val urls = urls
        }
    }

    private fun injectStyleUnsafe(styleString: String) {
        injectedStylesElement().appendText(styleString)
    }

    private fun injectedStylesElement(): Element {
        if (injectedStylesElement == null) {
            injectedStylesElement = document.createElement("style")
            getHead().appendChild(injectedStylesElement!!)
        }
        return injectedStylesElement!!
    }

    private fun getHead(): Element {
        val head: Element
        if (document.head == null) {
            head = document.createElement("head") as Element
            document.appendChild(head)
        } else {
            head = document.head!!
        }
        return head
    }

    actual fun importStyleSheet(url: String, method: StyleSheetImportMethod) {
        if (importedStyleSheets.urls.add(url)) {
            when(method) {
                StyleSheetImportMethod.LINK -> {
                    val injectedStyleSheetElement = document.createElement("link")
                    val rel = document.createAttribute("rel")
                    rel.value = "stylesheet"
                    val type = document.createAttribute("type")
                    type.value = "text/css"
                    val href = document.createAttribute("href")
                    href.value = url
                    injectedStyleSheetElement.attributes.setNamedItem(rel)
                    injectedStyleSheetElement.attributes.setNamedItem(type)
                    injectedStyleSheetElement.attributes.setNamedItem(href)
                    getHead().appendChild(injectedStyleSheetElement)
                }
                StyleSheetImportMethod.STYLE_IMPORT -> {
                    val injectedStyleSheetElement = document.createElement("style")
                    //TODO: Escape URL - https://developer.mozilla.org/en-US/docs/Web/CSS/url()#values
                    injectedStyleSheetElement.textContent = "@import url('$url');\n"
                    getHead().appendChild(injectedStyleSheetElement)
                }
            }
        }
    }

    actual fun importStyleSheet(styleSheet: StyleSheet) {
        if (importedStyleSheets.names.add(styleSheet.name)) {
            injectStyleUnsafe(styleSheet.render())
        }
    }
}

