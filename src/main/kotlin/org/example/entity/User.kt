package org.example.entity

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.example.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.concurrent.CompletableFuture

@Table("users")
data class User(
    @Id override val id: ID = ID.Empty,

    val email: String,
    val name: String?,
    val hashSalt: String,
    val roles: List<String>,
) : Node {

    fun posts(
        @GraphQLIgnore @Autowired postRepository: PostRepository,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Post>> {
        return postRepository.findAllByAuthorId(id, env)
    }
}
