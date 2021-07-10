package org.example.query

import com.expediagroup.graphql.server.operations.Query
import org.example.entity.Post
import org.example.input.PostWhereUniqueInput
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
