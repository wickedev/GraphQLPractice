@file:Suppress("unused")

package org.example.resolvers.post

import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import graphql.schema.DataFetchingEnvironment
import org.example.channel.PostCreatedChannel
import org.example.configuration.graphql.IsAuthenticated
import org.example.entity.Post
import org.example.service.PostService
import org.example.util.asFlux
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


data class Fuck<T>(val value: T)

@Component
class PostQuery(private val postService: PostService) : Query {

    private val log = LoggerFactory.getLogger(PostQuery::class.java)

    suspend fun post(where: PostWhereUniqueInput): Post? {
        log.info("post() called with: where = $where")
        return postService.post(where)
    }

    suspend fun posts(): List<Post> {
        log.info("posts() called")
        return postService.posts()
    }
}


@Component
class PostSubscription(private val postCreatedChannel: PostCreatedChannel) : Subscription {
    private val log = LoggerFactory.getLogger(PostSubscription::class.java)

    @IsAuthenticated
    fun posts(): Flux<Post> {
        log.info("posts() called")
        return postCreatedChannel.asFlux()
    }
}


@Component
class PostMutation(
    private val postService: PostService,
) : Mutation {

    private val log = LoggerFactory.getLogger(PostMutation::class.java)

    @IsAuthenticated
    suspend fun createPost(data: PostCreateInput): Post {
        log.info("createPost() called with: data = $data")
        return postService.createPost(data)
    }

    @IsAuthenticated
    suspend fun updatePost(data: PostUpdateInput, where: PostWhereUniqueInput): Post? {
        log.info("updatePost() called with: data = $data, where = $where")
        return postService.updatePost(data, where)
    }

    @IsAuthenticated
    suspend fun deletePost(where: PostWhereUniqueInput): Post? {
        log.info("deletePost() called with: where = $where")
        return postService.deletePost(where)
    }
}