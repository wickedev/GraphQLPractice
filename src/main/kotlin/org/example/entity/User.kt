package org.example.entity

import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table

@Table
data class User(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val email: String,
    val name: String?,
    val hashSalt: String,
    val role: Role
) {
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
}


@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
operator fun User.Role.compareTo(role: User.Role): Int {
    return if (this == User.Role.ADMIN && role == User.Role.USER) {
        1
    } else if (this == User.Role.USER && role == User.Role.ADMIN) {
        -1
    } else {
        -1
    }
}