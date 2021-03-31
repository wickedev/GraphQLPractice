package org.example.resolvers.post

import org.example.resolvers.common.BoolFieldUpdateOperationsInput
import org.example.resolvers.common.NullableStringFieldUpdateOperationsInput
import org.example.resolvers.common.StringFieldUpdateOperationsInput
import org.example.resolvers.user.UserCreateNestedOneWithoutPostsInput
import org.example.util.Identifier

data class PostCreateInput(
    val title: String,
    val content: String?,
    val published: Boolean?,
    val author: UserCreateNestedOneWithoutPostsInput?
)

data class PostUpdateInput(
    val content: NullableStringFieldUpdateOperationsInput?,
    val published: BoolFieldUpdateOperationsInput?,
    val title: StringFieldUpdateOperationsInput?
)

data class PostWhereUniqueInput(
    val id: Identifier
)