package br.com.devsrsouza.kotlinx.serialization.sql.internal

import br.com.devsrsouza.kotlinx.serialization.sql.AutoIncrement
import br.com.devsrsouza.kotlinx.serialization.sql.PrimaryKey
import br.com.devsrsouza.kotlinx.serialization.sql.Unique
import br.com.devsrsouza.kotlinx.serialization.sql.UnsupportedOperation
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor

internal fun SerialDescriptor.getSqlClassDescriptor(
    elementIndex: Int
) = SqlClassDescriptor(
    isPrimaryKey(elementIndex),
    isUnique(elementIndex),
    isAutoIncrement(elementIndex)
)

internal fun SerialDescriptor.isPrimaryKey(
    elementIndex: Int
) = findElementAnnotation<PrimaryKey>(elementIndex) != null

internal fun SerialDescriptor.isUnique(
    elementIndex: Int
) = findElementAnnotation<Unique>(elementIndex) != null

internal fun SerialDescriptor.isAutoIncrement(
    elementIndex: Int
) = findElementAnnotation<AutoIncrement>(elementIndex) != null

internal val SerialDescriptor.simpleSerialName get() = serialName.substringAfterLast('.')

internal fun mapValues(
    kind: PrimitiveKind
): String = when(kind) {
    PrimitiveKind.BOOLEAN -> "BOOLEAN"
    PrimitiveKind.CHAR -> "CHAR"
    PrimitiveKind.BYTE -> "TINYINT"
    PrimitiveKind.SHORT -> "SMALLINT"
    PrimitiveKind.INT -> "INT"
    PrimitiveKind.LONG -> "BIGINT"
    PrimitiveKind.FLOAT -> "FLOAT"
    PrimitiveKind.DOUBLE -> "DOUBLE"
    PrimitiveKind.STRING -> "TEXT"
    else -> ""
}

internal fun isStringType(
    kind: PrimitiveKind
): Boolean = kind.equalsAny(
    PrimitiveKind.CHAR,
    PrimitiveKind.STRING
)

internal fun isNumberType(
    kind: PrimitiveKind
): Boolean = kind.equalsAny(
    PrimitiveKind.BYTE,
    PrimitiveKind.SHORT,
    PrimitiveKind.INT,
    PrimitiveKind.LONG,
    PrimitiveKind.FLOAT,
    PrimitiveKind.DOUBLE
)

inline fun nameWithEscape(
    value: String
) = "`$value`"

internal fun formatToStringType(
    value: String
) = "'$value'"

internal inline fun <reified A: Annotation> SerialDescriptor.findElementAnnotation(
    elementIndex: Int
): A? {
    return getElementAnnotations(elementIndex).find { it is A } as A?
}

internal inline fun <reified A: Annotation> SerialDescriptor.findEntityAnnotation(): A? {
    return annotations.find { it is A } as A?
}

internal inline fun SerialDescriptor.requirePrimitiveKind(): PrimitiveKind {
    return kind as? PrimitiveKind
        ?: throw UnsupportedOperation("Currently is only supported Primitive types.")
}

inline fun SerialDescriptor.forEachElement(
    filter: (
        elementIndex: Int,
        elementName: String,
        elementDescriptor: SerialDescriptor
    ) -> Boolean = { _, _, _ -> true },
    block: (
        index: Int,
        maxValues: Int,
        elementIndex: Int,
        elementName: String,
        elementDescriptor: SerialDescriptor
    ) -> Unit
) {
    val properties = (0 until elementsCount).filter {
        filter(it, getElementName(it), getElementDescriptor(it))
    }
    for((i, elementIndex) in properties.withIndex()) {
        block(
            i,
            properties.size,
            elementIndex,
            getElementName(elementIndex),
            getElementDescriptor(elementIndex)
        )
    }
}

internal fun Any.equalsAny(vararg values: Any): Boolean {
    return values.any { it == this }
}