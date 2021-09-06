package org.carrat.web.css

import kotlinx.css.RuleSet
import org.carrat.context.HasContext
import org.carrat.experimental.CarratExperimental
import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.web.builder.html.*
import org.carrat.web.webapi.Element
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@CarratExperimental
public open class StyleSheet(name: String, ruleSet: RuleSet? = null) {
    internal val name: String = name.replace(".", "_")
    internal val classes: MutableList<Pair<String, RuleSet>> = mutableListOf()
    internal val globals: MutableList<RuleSet> = mutableListOf()

    init {
        if (ruleSet != null) {
            globals += ruleSet
        }
    }

    public fun include(ruleSet: RuleSet) {
        globals += ruleSet
    }

    public fun css(builder: RuleSet): CssClassProvider = CssClassProvider(builder)

    public fun <T, E : Element> styled(
        tagConstructor: TagType<T, E>,
        css: RuleSet
    ): StyledProvider<T, E>
            where T : CommonAttributeGroup<E>,
                  T : Tag<T, E> = StyledProvider(tagConstructor, css)

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

    public class StyledProvider<T, E : Element>(
        private val tagType: TagType<T, E>,
        private val builder: RuleSet
    )
            where T : CommonAttributeGroup<E>,
                  T : Tag<T, E> {
        public operator fun provideDelegate(
            sheet: StyleSheet,
            property: KProperty<*>
        ): ReadOnlyProperty<StyleSheet, Styled<T, E>> {
            val className = sheet.getClassName(property)
            sheet.classes.add(className to builder)
            return StyledHolder(tagType, className)
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

    public class StyledHolder<T, E : Element>(
        private val tagType: TagType<T, E>,
        private val className: String
    ) : ReadOnlyProperty<StyleSheet, Styled<T, E>>
            where T : CommonAttributeGroup<E>,
                  T : Tag<T, E> {
        @OptIn(ExperimentalMultipleReceivers::class)
        override fun getValue(thisRef: StyleSheet, property: KProperty<*>): Styled<T, E> {
            return fun TagConsumer<*>.(block: StyledBlock<T, E>) {
                context[styleSheetsManager].importStyleSheet(thisRef)
                tag(tagType) {
                    attributes {
                        `class` = className
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
