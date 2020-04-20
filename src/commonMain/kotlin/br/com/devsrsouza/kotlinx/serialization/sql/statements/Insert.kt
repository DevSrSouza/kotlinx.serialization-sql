package br.com.devsrsouza.kotlinx.serialization.sql.statements

import br.com.devsrsouza.kotlinx.serialization.sql.internal.*
import br.com.devsrsouza.kotlinx.serialization.sql.internal.formatToStringType
import br.com.devsrsouza.kotlinx.serialization.sql.internal.getSqlClassDescriptor
import br.com.devsrsouza.kotlinx.serialization.sql.internal.isStringType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

fun <T> insertQuery(
    serializer: KSerializer<T>,
    value: T
): String {
    val desc = serializer.descriptor
    // TODO: migrate to a StringFormat for Insertion
    // OR create a TaggedDecoder just
    // serializer.deserialize()
    val element = Json.toJson(serializer, value).jsonObject

    var query = "INSERT INTO "
    query += desc.serialName
    query += " "

    var columnQuery = "("
    var valuesQuery = "("

    desc.forEachElement(
        filter = { elementIndex, _, elementDesc ->
            val (_, _, isAutoIncrement) = desc.getSqlClassDescriptor(elementIndex)

            // if is auto increment, skip it
            !isAutoIncrement && !elementDesc.isNullable
        }
    ) { i, max, elementIndex, elementName, elementDesc ->
        val (_, _, isAutoIncrement) = desc.getSqlClassDescriptor(elementIndex)

        val kind = elementDesc.requirePrimitiveKind()

        val value = element.get(elementName)!!.primitive.toString()

        columnQuery += nameWithEscape(elementName)
        valuesQuery += if (isStringType(kind))
            formatToStringType(value)
        else value

        if (i < max-1) {
            columnQuery += ", "
            valuesQuery += ", "
        }

    }

    columnQuery += ")"
    valuesQuery += ")"

    query += columnQuery
    query += " values "
    query += valuesQuery
    query += ";"

    return query
}