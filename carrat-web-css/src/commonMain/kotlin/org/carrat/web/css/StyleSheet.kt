package org.carrat.web.css

import kotlinx.css.RuleSet
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.classes
import org.carrat.web.builder.CBuilder
import org.carrat.web.builder.TagConstructor
import org.carrat.web.builder.attributes
import org.carrat.web.builder.tag
import org.carrat.context.HasContext
import org.carrat.experimental.CarratExperimental
import org.carrat.experimental.ExperimentalMultipleReceivers
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@CarratExperimental
public open class StyleSheet(name: String, ruleSet: RuleSet? = null) {
    internal val name: String = name.replace(".", "_")
    internal val classes: MutableList<Pair<String, RuleSet>> = mutableListOf()
    internal val globals: MutableList<RuleSet> = mutableListOf()

    init {
        if(ruleSet != null) {
            globals += ruleSet
        }
    }

    public fun include(ruleSet: RuleSet) {
        globals += ruleSet
    }

    public fun css(builder: RuleSet): CssClassProvider = CssClassProvider(builder)

    public fun <T : CommonAttributeGroupFacade> styled(
        tagConstructor: TagConstructor<T>,
        css: RuleSet
    ): StyledProvider<T> = StyledProvider(tagConstructor, css)

    public class CssClassProvider(public val builder: RuleSet) {
        public operator fun provideDelegate(
            sheet: StyleSheet,
            property: KProperty<*>
        ): ReadOnlyProperty<StyleSheet, HasContext?.() -> String> {
            val className = sheet.getClassName(property)
            sheet.classes.add(className to builder)
            return CssHolder(className)
        }
    }

    public class StyledProvider<T : CommonAttributeGroupFacade>(
        private val tagConstructor: TagConstructor<T>,
        private val builder: RuleSet
    ) {
        public operator fun provideDelegate(
            sheet: StyleSheet,
            property: KProperty<*>
        ): ReadOnlyProperty<StyleSheet, Styled<T>> {
            val className = sheet.getClassName(property)
            sheet.classes.add(className to builder)
            return StyledHolder(tagConstructor, className)
        }
    }

    public class CssHolder(private val className: String) : ReadOnlyProperty<StyleSheet, HasContext?.() -> String> {
        override fun getValue(thisRef: StyleSheet, property: KProperty<*>): HasContext?.() -> String {
            return {
                if (this != null) {
                    context.get(styleSheetsManager).importStyleSheet(thisRef)
                }
                className
            }
        }
    }

    public class StyledHolder<T : CommonAttributeGroupFacade>(
        private val tagConstructor: TagConstructor<T>,
        private val className: String
    ) : ReadOnlyProperty<StyleSheet, Styled<T>> {
        @OptIn(ExperimentalMultipleReceivers::class)
        override fun getValue(thisRef: StyleSheet, property: KProperty<*>): Styled<T> {
            return fun CBuilder.(block: StyledBlock<T>) {
                context.get(styleSheetsManager).importStyleSheet(thisRef)
                tag(tagConstructor) {
                    attributes {
                        classes = setOf(className)
                    }
                    block()
                }
            }
        }
    }

    private fun getClassName(property: KProperty<*>): String {
        return "${name}-${property.name}"
    }
}
