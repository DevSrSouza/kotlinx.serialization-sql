package br.com.devsrsouza.kotlinx.serialization.sql

import kotlinx.serialization.SerialInfo

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class PrimaryKey

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class Unique

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class AutoIncrement