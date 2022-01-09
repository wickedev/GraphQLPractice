package org.example.resolver

import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.Auth
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLNodeRepository
import io.github.wickedev.graphql.types.Backward
import io.github.wickedev.graphql.types.ID
import kotlinx.coroutines.future.await
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.example.types.PostConnect
import org.example.types.PostEdge
import org.example.types.UserConnect
import org.example.types.UserEdge
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class BlogQuery(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val nodeRepository: GraphQLNodeRepository,
) : Query {

    suspend fun node(id: ID, env: DataFetchingEnvironment): Node? {
        return nodeRepository.findNodeById(id, env).await()
    }

    fun users(last: Int?, before: ID?, env: DataFetchingEnvironment): CompletableFuture<UserConnect> {
        return userRepository.connection(Backward(last, before), env)
            .thenApply { UserConnect(it.edges.map { e -> UserEdge(e.node, e.cursor) }, it.pageInfo) }
    }

    fun posts(last: Int?, before: ID?, env: DataFetchingEnvironment): CompletableFuture<PostConnect> {
        return postRepository.connection(Backward(last, before), env)
            .thenApply { PostConnect(it.edges.map { e -> PostEdge(e.node, e.cursor) }, it.pageInfo) }
    }
}
