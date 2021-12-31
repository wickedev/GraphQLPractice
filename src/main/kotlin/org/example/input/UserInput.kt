package org.example.input

import io.github.wickedev.graphql.types.ID

data class UserCreateInput(
    val email: String,
    val password: String,
    val name: String?,
)

data class UserCreateNestedOneWithoutPostsInput(
    val connect: UserWhereUniqueInput
)

data class UserWhereUniqueInput(
    val email: String?,
    val id: ID?
)

data class UserUpdateInput(
    val email: StringFieldUpdateOperationsInput?,
    val name: NullableStringFieldUpdateOperationsInput?,
)