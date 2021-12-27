package org.example.entity

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class User(
    @Id override val id: ID = ID.Empty,
    val email: String,
    val name: String?,
    val hashSalt: String,
    val role: String,
) : Node
