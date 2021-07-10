package org.example.mutation

import com.expediagroup.graphql.server.operations.Mutation
import org.example.configuration.graphql.IsAuthenticated
import org.example.entity.Post
import org.example.input.PostCreateInput
import org.example.input.PostUpdateInput
import org.example.input.PostWhereUniqueInput
import org.example.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


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