package org.example.util


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPEALIAS)
annotation class Id

@Id
typealias Identifier = Long

const val DEFAULT_ID_VALUE = 0L