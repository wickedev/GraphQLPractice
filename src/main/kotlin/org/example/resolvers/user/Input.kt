package org.example.resolvers.user

import org.example.resolvers.common.NullableStringFieldUpdateOperationsInput
import org.example.resolvers.common.StringFieldUpdateOperationsInput
import org.example.util.Identifier


data class UserCreateInput(
    val email: String,
    val name: String?
)

data class UserCreateNestedOneWithoutPostsInput(
    val connect: UserWhereUniqueInput
)

data class UserWhereUniqueInput(
    val email: String?,
    val id: Identifier?
)

data class UserUpdateInput(
    val email: StringFieldUpdateOperationsInput?,
    val name: NullableStringFieldUpdateOperationsInput?,
)