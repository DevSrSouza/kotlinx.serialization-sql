package br.com.devsrsouza.kotlinx.serialization.sql.statements

import br.com.devsrsouza.kotlinx.serialization.sql.UnsupportedOperation
import br.com.devsrsouza.kotlinx.serialization.sql.internal.*
import br.com.devsrsouza.kotlinx.serialization.sql.internal.getSqlClassDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StructureKind

fun createTable(serializer: KSerializer<*>): String {
    val desc = serializer.descriptor

    if(desc.kind !is StructureKind.CLASS)
        throw UnsupportedOperation("You can't create Table from a SerialKind that is not Class.")

    var queryBuilder = "CREATE TABLE IF NOT EXISTS "
    queryBuilder += desc.simpleSerialName
    queryBuilder += "("

    var hasPrimaryKey = false

    desc.forEachElement { _, _, elementIndex, elementName, elementDesc ->

        val isNullable = elementDesc.isNullable
        val (isPrimaryKey, isUnique, isAutoIncrement) = desc.getSqlClassDescriptor(elementIndex)

        if(isPrimaryKey && isUnique)
            throw UnsupportedOperation("You can not have a property that is PRIMARY KEY and UNIQUE.")

        if(isPrimaryKey && isNullable)
            throw UnsupportedOperation("You can not have a primary key nullable.")

        if (isPrimaryKey && hasPrimaryKey)
            throw UnsupportedOperation("You can not have two properties with PRIMARY KEY.")

        val kind = elementDesc.requirePrimitiveKind()

        if(isAutoIncrement && !isNumberType(kind))
            throw UnsupportedOperation("Auto Increment can not be usage at non number type.")

        queryBuilder += nameWithEscape(elementName)
        queryBuilder += " "
        queryBuilder += mapValues(kind)

        queryBuilder += " "
        queryBuilder += if(isNullable) "NULL" else "NOT NULL"

        if(isPrimaryKey) {
            queryBuilder += " PRIMARY KEY"
            hasPrimaryKey = true
        }

        if(isUnique)
            queryBuilder += " UNIQUE"

        // CHECK if is number type
        if(isAutoIncrement)
            queryBuilder += " AUTO_INCREMENT"

        if (elementIndex < desc.elementsCount - 1)
            queryBuilder += ", "
    }
    queryBuilder += ");"

    return queryBuilder
}