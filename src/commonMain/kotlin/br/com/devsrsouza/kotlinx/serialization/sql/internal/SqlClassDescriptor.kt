package br.com.devsrsouza.kotlinx.serialization.sql.internal

data class SqlClassDescriptor(
    val isPrimaryKey: Boolean,
    val isUnique: Boolean,
    val isAutoIncrement: Boolean
)