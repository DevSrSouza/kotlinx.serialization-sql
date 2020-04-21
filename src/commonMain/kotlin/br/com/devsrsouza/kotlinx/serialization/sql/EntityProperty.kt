package br.com.devsrsouza.kotlinx.serialization.sql

import kotlin.reflect.KProperty1

val <T, V> KProperty1<T, V>.entity
    get() = EntityProperty(this)

data class EntityProperty<T, V>(
    private val property: KProperty1<T, V>
) {
    fun where(
        operator: Op,
        value: V
    ) = Where<T, V>(property, value, operator)
}
