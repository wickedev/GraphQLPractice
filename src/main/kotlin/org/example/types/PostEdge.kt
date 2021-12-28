package org.example.types

import io.github.wickedev.graphql.types.ConnectionCursor
import org.example.entity.Post

data class PostEdge(
    val node: Post,
    val cursor: ConnectionCursor
)