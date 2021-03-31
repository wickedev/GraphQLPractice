package org.example.service

import org.example.channel.PostCreatedChannel
import org.example.entity.Post
import org.example.repository.PostRepository
import org.example.resolvers.post.PostCreateInput
import org.example.resolvers.post.PostUpdateInput
import org.example.resolvers.post.PostWhereUniqueInput
import org.example.util.Identifier
import org.example.util.coroutine.flux.await
import org.example.util.coroutine.mono.await
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postCreatedChannel: PostCreatedChannel,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(PostService::class.java)

    suspend fun post(where: PostWhereUniqueInput): Post? {
        log.info("post() called with: where = $where")
        return postRepository.findById(where.id).await()
    }

    suspend fun posts(): List<Post> {
        log.info("posts() called")
        return postRepository.findAll().await()
    }

    suspend fun postsByAuthorId(id: Identifier): List<Post> {
        return postRepository.findByAuthorId(id).await()
    }

    @Transactional
    suspend fun createPost(data: PostCreateInput): Post {
        log.info("createPost() called with: data = $data")

        val user = data.author?.connect?.let { userService.user(it) }

        val post = postRepository.save(
            Post(
                title = data.title,
                content = data.content,
                published = data.published ?: false,
                authorId = user?.id
            )
        ).await()
        postCreatedChannel.offer(post)
        return post
    }

    @Transactional
    suspend fun updatePost(data: PostUpdateInput, where: PostWhereUniqueInput): Post? {
        log.info("updatePost() called with: data = $data, where = $where")

        val post = postRepository.findById(where.id).await()
            ?: return null

        return postRepository.save(
            post.copy(
                content = if (data.content != null) data.content.set else post.content,
                title = if (data.title != null) data.title.set else post.title,
                published = if (data.published != null) data.published.set else post.published,
            )
        ).await()
    }

    @Transactional
    suspend fun deletePost(where: PostWhereUniqueInput): Post? {
        log.info("deletePost() called with: where = $where")

        val post = postRepository.findById(where.id).await()
            ?: null
        postRepository.deleteById(where.id).await()
        return post
    }
}