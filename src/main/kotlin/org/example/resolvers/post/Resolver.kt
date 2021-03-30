@file:Suppress("unused")

package org.example.resolvers.post

import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import org.example.model.Post
import org.example.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

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
class PostMutation(
    private val postService: PostService,
) : Mutation {

    private val log = LoggerFactory.getLogger(PostMutation::class.java)

    suspend fun createPost(data: PostCreateInput): Post {
        log.info("createPost() called with: data = $data")
        return postService.createPost(data)
    }

    suspend fun updatePost(data: PostUpdateInput, where: PostWhereUniqueInput): Post? {
        log.info("updatePost() called with: data = $data, whereUniqueInput = $where")
        return postService.updatePost(data, where)
    }

    suspend fun deletePost(where: PostWhereUniqueInput): Post? {
        log.info("deletePost() called with: where = $where")
        return postService.deletePost(where)
    }
}
