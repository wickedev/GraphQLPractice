package org.example.resolvers.common

data class StringFieldUpdateOperationsInput(
    val set: String
)

data class NullableStringFieldUpdateOperationsInput(
    val set: String?
)


data class BoolFieldUpdateOperationsInput(
    val set: Boolean
)