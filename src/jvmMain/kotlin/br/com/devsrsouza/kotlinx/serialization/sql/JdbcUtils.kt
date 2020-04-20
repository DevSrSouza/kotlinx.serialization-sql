package br.com.devsrsouza.kotlinx.serialization.sql

import java.sql.ResultSet

fun ResultSet.toList(): List<Map<String, Any>> {
    val metadata = metaData
    val columns = metadata.columnCount

    val list = ArrayList<Map<String, Any>>(50)
    while (next()) {
        list += (1..columns).associate {
            metadata.getColumnName(it) to getObject(it)
        }
    }

    return list
}