package org.example.input

import io.github.wickedev.graphql.types.ID

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
    val id: ID?
)