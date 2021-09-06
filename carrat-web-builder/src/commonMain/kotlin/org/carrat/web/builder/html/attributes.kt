package org.carrat.web.builder.html

import kotlin.collections.Map

public abstract class Attribute<T>() {
    public open operator fun get(thisRef: TagHandler<*>, attributeName: String): T? =
        thisRef.getAttribute(attributeName)?.let {
            decode(attributeName, it)
        }

    public open operator fun set(thisRef: TagHandler<*>, attributeName: String, value: T?) {
        val encodedValue = if (value != null) {
            encode(attributeName, value)
        } else {
            null
        }
        thisRef.setAttribute(attributeName, encodedValue)
    }

    protected abstract fun encode(attributeName: String, value: T): String
    protected abstract fun decode(attributeName: String, value: String): T
}

public class StringAttribute : Attribute<String>() {
    override fun encode(attributeName: String, value: String): String = value
    override fun decode(attributeName: String, value: String): String = value
}

private fun Boolean.booleanEncode() = toString()

public class BooleanAttribute(
    public val trueValue: String = "true",
    public val falseValue: String = "false"
) : Attribute<Boolean>() {
    override fun encode(attributeName: String, value: Boolean): String = if (value) trueValue else falseValue
    override fun decode(attributeName: String, value: String): Boolean = when (value) {
        trueValue -> true
        falseValue -> false
        else -> throw IllegalArgumentException("Unknown value $value for $attributeName")
    }
}

private fun Boolean.tickerEncode(attributeName: String): String = if (this) attributeName else ""

public class TickerAttribute : Attribute<Boolean>() {
    override fun set(thisRef: TagHandler<*>, attributeName: String, value: Boolean?) {
        if (value != false) {
            thisRef.setAttribute(attributeName, attributeName)
        } else {
            thisRef.setAttribute(attributeName, null)
        }
    }

    override fun encode(attributeName: String, value: Boolean): String = value.tickerEncode(attributeName)
    override fun decode(attributeName: String, value: String): Boolean = value == attributeName
}

public class PositiveIntegerAttribute : Attribute<UInt>() {
    override fun encode(attributeName: String, value: UInt): String {
        check(value > 0u) { "Value must be strongly positive" }
        return value.toString()
    }

    override fun decode(attributeName: String, value: String): UInt = value.toUInt()
}

public class FloatAttribute : Attribute<Float>() {
    override fun encode(attributeName: String, value: Float): String = value.toString()

    override fun decode(attributeName: String, value: String): Float = value.toFloat()
}

public class IntegerAttribute : Attribute<Int>() {
    override fun encode(attributeName: String, value: Int): String = value.toString()

    override fun decode(attributeName: String, value: String): Int = value.toInt()
}

public class EnumAttribute<T : AttributeEnum>(private val values: Map<String, T>) : Attribute<T>() {
    override fun encode(attributeName: String, value: T): String = value.string
    override fun decode(attributeName: String, value: String): T =
        values[value] ?: throw IllegalArgumentException("Unknown value $value for $attributeName")
}

internal val stringAttributeType: Attribute<String> = StringAttribute()

internal val booleanAttributeType: Attribute<Boolean> = BooleanAttribute()

internal val onOffAttributeType: Attribute<Boolean> = BooleanAttribute("on", "off")

internal val tickerAttributeType: Attribute<Boolean> = TickerAttribute()

internal val positiveIntegerAttributeType: Attribute<UInt> = PositiveIntegerAttribute()

internal val floatAttributeType: Attribute<Float> = FloatAttribute()

internal val integerAttributeType: Attribute<Int> = IntegerAttribute()