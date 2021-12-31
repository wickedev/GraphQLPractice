package org.example.input

data class StringFieldUpdateOperationsInput(
    val set: String
)

data class NullableStringFieldUpdateOperationsInput(
    val set: String?
)

data class BoolFieldUpdateOperationsInput(
    val set: Boolean
)