package org.example.input

import io.github.wickedev.graphql.types.ID

data class PostCreateInput(
    val title: String,
    val content: String?,
    val published: Boolean?,
    val author: UserCreateNestedOneWithoutPostsInput?
)

data class PostUpdateInput(
    val title: StringFieldUpdateOperationsInput?,
    val content: NullableStringFieldUpdateOperationsInput?,
    val published: BoolFieldUpdateOperationsInput?,
)

data class PostWhereUniqueInput(
    val id: ID
)