package org.example.input

import org.example.util.Identifier


data class UserCreateInput(
    val email: String,
    val name: String?
)

data class UserCreateNestedOneWithoutPostsInput(
    val connect: UserWhereUniqueInput
)

data class UserWhereUniqueInput(
    val email: String? = null,
    val id: Identifier? = null
)

data class UserUpdateInput(
    val email: StringFieldUpdateOperationsInput?,
    val name: NullableStringFieldUpdateOperationsInput?,
)