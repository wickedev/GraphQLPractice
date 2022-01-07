package org.example.entity

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.Backward
import io.github.wickedev.graphql.types.ID
import org.example.interfaces.SimpleUserDetails
import org.example.repository.PostRepository
import org.example.types.PostConnect
import org.example.types.PostEdge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.concurrent.CompletableFuture

@Table("users")
data class User(
    @Id override val id: ID = ID.Empty,

    val email: String,
    val name: String?,
    @GraphQLIgnore
    val hashSalt: String,
    val roles: List<String>,
) : Node, SimpleUserDetails {

    fun posts(
        last: Int?, before: ID?,
        @GraphQLIgnore @Autowired postRepository: PostRepository,
        env: DataFetchingEnvironment
    ): CompletableFuture<PostConnect> {
        return postRepository.connectionByAuthorId(id, Backward(last, before), env)
            .thenApply { PostConnect(it.edges.map { e -> PostEdge(e.node, e.cursor) }, it.pageInfo) }
    }

    @GraphQLIgnore
    override fun getUsername(): String = email

    @GraphQLIgnore
    override fun getPassword(): String = hashSalt

    @GraphQLIgnore
    override fun getAuthorities(): Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority(it) }
}
