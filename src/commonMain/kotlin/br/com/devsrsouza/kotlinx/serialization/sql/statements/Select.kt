package br.com.devsrsouza.kotlinx.serialization.sql.statements

import br.com.devsrsouza.kotlinx.serialization.sql.internal.isStringType
import br.com.devsrsouza.kotlinx.serialization.sql.internal.nameWithEscape
import br.com.devsrsouza.kotlinx.serialization.sql.internal.requirePrimitiveKind
import br.com.devsrsouza.kotlinx.serialization.sql.internal.simpleSerialName
import kotlinx.serialization.*
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.reflect.KProperty1

data class Where<T, V>(
    val property: KProperty1<T, V>,
    val value: V
)

fun <T> selectQuery(
    serializer: KSerializer<T>,
    where: List<Where<T, *>>
): Pair<String, (values: List<Map<String, Any?>>) -> List<T>> {
    val desc = serializer.descriptor

    var queryBuilder = "SELECT * FROM "
    queryBuilder += desc.simpleSerialName

    val whereQuery = where.map { it.property.name to it.value }
        .joinToString { (elementName, value) ->
        val elementDesc = desc.getElementDescriptor(desc.getElementIndex(elementName))

        val kind = elementDesc.requirePrimitiveKind()

        val value = value.let {
            if(isStringType(kind)) "'$it'" else it
        }

        "${nameWithEscape(elementName)}=$value"
    }

    if(where.isNotEmpty()) {
        queryBuilder += " WHERE "
        queryBuilder += whereQuery
    }

    queryBuilder += ";"

    return queryBuilder to { values -> resultMapper(serializer, values) }
}

private fun <T> resultMapper(
    serializer: KSerializer<T>,
    values: List<Map<String, Any?>>
): List<T> {
    return values.map {
        serializer.deserialize(ResultDecoder(it))
    }
}


@OptIn(InternalSerializationApi::class)
private class ResultDecoder(
    private val values: Map<String, Any?>
) : NamedValueDecoder() {
    private var currentIndex = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if(descriptor.elementsCount == currentIndex)
            CompositeDecoder.READ_DONE
        else currentIndex++
    }

    override fun decodeTaggedBoolean(tag: String): Boolean {
        return values[tag] as Boolean
    }

    override fun decodeTaggedByte(tag: String): Byte {
        return (values[tag] as Number).toByte()
    }

    override fun decodeTaggedChar(tag: String): Char {
        return values[tag] as Char
    }

    override fun decodeTaggedDouble(tag: String): Double {
        return (values[tag] as Number).toDouble()
    }

    /*// TODO
    override fun decodeTaggedEnum(tag: String, enumDescription: SerialDescriptor): Int {
        return super.decodeTaggedEnum(tag, enumDescription)
    }*/

    override fun decodeTaggedFloat(tag: String): Float {
        return (values[tag] as Number).toFloat()
    }

    override fun decodeTaggedInt(tag: String): Int {
        return (values[tag] as Number).toInt()
    }

    override fun decodeTaggedLong(tag: String): Long {
        return (values[tag] as Number).toLong()
    }

    override fun decodeTaggedShort(tag: String): Short {
        return (values[tag] as Number).toShort()
    }

    override fun decodeTaggedString(tag: String): String {
        return values[tag] as String
    }

    override fun decodeTaggedNotNullMark(tag: String): Boolean {
        return values[tag] != null
    }
}
