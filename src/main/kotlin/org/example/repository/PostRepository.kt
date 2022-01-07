package org.example.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.types.Backward
import io.github.wickedev.graphql.types.Connection
import io.github.wickedev.graphql.types.ID
import org.example.entity.Post
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture


@Repository
interface PostRepository : GraphQLR2dbcRepository<Post>, CustomPostRepository {
    fun connectionByAuthorId(authorId: ID, backward: Backward, env: DataFetchingEnvironment): CompletableFuture<Connection<Post>>
}

interface CustomPostRepository {
    fun findBy(criteria: Criteria): Mono<Post?>

    fun update(criteria: Criteria, update: Update): Mono<Int>

    fun delete(criteria: Criteria): Mono<Int>
}

@Repository
class PostRepositoryImpl(
    private val entityTemplate: R2dbcEntityTemplate
) : CustomPostRepository {
    override fun findBy(criteria: Criteria): Mono<Post?> {
        return entityTemplate.select(Query.query(criteria).limit(1), Post::class.java)
            .singleOrEmpty()
    }

    override fun update(criteria: Criteria, update: Update): Mono<Int> {
        return entityTemplate.update(Query.query(criteria), update, Post::class.java)
    }

    override fun delete(criteria: Criteria): Mono<Int> {
        return entityTemplate.delete(Query.query(criteria), Post::class.java)
    }
}