package br.com.devsrsouza.kotlinx.serialization.sql

import kotlin.reflect.KProperty1

typealias Op = WhereOperator

data class Where<T, V> internal constructor(
    val property: KProperty1<T, V>,
    val value: V,
    val operator: WhereOperator
)

enum class WhereOperator(val operator: String) {
    eq("="),
    greater(">"),
    minor("<")
}