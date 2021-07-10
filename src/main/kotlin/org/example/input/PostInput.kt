package org.example.input

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