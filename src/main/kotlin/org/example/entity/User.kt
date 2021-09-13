package org.example.entity

import org.example.configuration.r2dbc.Node
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table

@Table
data class User(
    @Id override val id: Identifier = DEFAULT_ID_VALUE,
    val email: String,
    val name: String?,
    val hashSalt: String,
    val role: Role
) : Node {
    companion object {
        private val log = LoggerFactory.getLogger(User::class.java)
    }

    enum class Role {
        ADMIN,
        USER;
    }

    @Transient
    lateinit var posts: List<Post>

    /*fun posts(env: DataFetchingEnvironment): CompletableFuture<List<Post>> {
        log.info("posts() called with: authorId: $id")
        return env.getDataLoader(PostsByAuthorIdDataLoader::class).load(id)
    }*/

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    operator fun Role.compareTo(role: Role): Int {
        return if (this == Role.ADMIN && role == Role.USER) {
            1
        } else if (this == Role.USER && role == Role.ADMIN) {
            -1
        } else {
            -1
        }
    }
}
