package org.example.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.types.ID
import org.example.entity.Post
import org.springframework.stereotype.Repository
import java.util.concurrent.CompletableFuture


@Repository
interface PostRepository : GraphQLR2dbcRepository<Post> {
    fun findAllByAuthorId(authorId: ID, env: DataFetchingEnvironment): CompletableFuture<List<Post>>
}
