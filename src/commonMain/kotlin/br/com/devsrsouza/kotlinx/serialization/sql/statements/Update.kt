package br.com.devsrsouza.kotlinx.serialization.sql.statements

import br.com.devsrsouza.kotlinx.serialization.sql.internal.*
import br.com.devsrsouza.kotlinx.serialization.sql.internal.isStringType
import br.com.devsrsouza.kotlinx.serialization.sql.internal.requirePrimitiveKind
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.content
import kotlin.reflect.KProperty1

// using primary key
fun <T> updateKeyQuery(
    serializer: KSerializer<T>,
    value: T,
    setProperty: List<KProperty1<T, *>>
): String {
    val desc = serializer.descriptor

    val primaryKeyElementIndex = (0 until desc.elementsCount)
        .firstOrNull { desc.getSqlClassDescriptor(it).isPrimaryKey }
        ?: throw UnsupportedOperationException("Can not use updateKeyQuery with a data class that has none Primary Key.")

    val primaryKeyElementName = desc.getElementName(primaryKeyElementIndex)

    return updateQueryWithName(
        serializer,
        value,
        setProperty.map { it.name },
        listOf(primaryKeyElementName)
    )
}

// using where
fun <T> updateQuery(
    serializer: KSerializer<T>,
    value: T,
    setProperty: List<KProperty1<T, *>>,
    whereProperty: List<KProperty1<T, *>>
): String {
    return updateQueryWithName(
        serializer,
        value,
        setProperty.map { it.name },
        whereProperty.map { it.name }
    )
}

private fun <T> updateQueryWithName(
    serializer: KSerializer<T>,
    value: T,
    setProperty: List<String>,
    whereProperty: List<String>
): String {
    if(setProperty.isEmpty())
        throw UnsupportedOperationException("setProperty can not be empty to generate a updateQuery.")

    val desc = serializer.descriptor
    val element = Json.toJson(serializer, value).jsonObject

    var queryBuilder = "UPDATE "
    queryBuilder += desc.simpleSerialName
    queryBuilder += " SET "

    fun List<String>.genereteQuery(): String {
        return joinToString { elementName ->
            val elementDesc = desc.getElementDescriptor(desc.getElementIndex(elementName))

            val kind = elementDesc.requirePrimitiveKind()

            val value = element.get(elementName)!!.content.let {
                if(isStringType(kind)) "'$it'" else it
            }

            "${nameWithEscape(elementName)}=$value"
        }
    }

    val setQuery = setProperty.genereteQuery()
    val whereQuery = whereProperty.genereteQuery()

    queryBuilder += setQuery

    if(whereProperty.isNotEmpty()) {
        queryBuilder += " WHERE "
        queryBuilder += whereQuery
    }

    queryBuilder += ";"

    return queryBuilder
}
