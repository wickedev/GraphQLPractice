package org.example.types

import io.github.wickedev.graphql.types.ConnectionCursor
import org.example.entity.User

data class UserEdge(
    val node: User,
    val cursor: ConnectionCursor
)