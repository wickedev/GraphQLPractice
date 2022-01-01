package org.example.entity

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.annotations.Relation
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

data class Post(
    @Id
    override val id: ID = ID.Empty,

    val title: String,
    val content: String?,
    val published: Boolean,
    val postedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null,

    @Relation(User::class) val authorId: ID,

) : Node {
    fun author(
        @GraphQLIgnore @Autowired userRepository: UserRepository,
        env: DataFetchingEnvironment
    ): CompletableFuture<User> {
        return userRepository.findById(authorId, env)
    }
}