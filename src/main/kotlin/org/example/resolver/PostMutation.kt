package org.example.resolver

import com.expediagroup.graphql.server.operations.Mutation
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.Auth
import io.github.wickedev.graphql.types.ID
import org.example.entity.Post
import org.example.input.PostCreateInput
import org.example.input.PostUpdateInput
import org.example.input.PostWhereUniqueInput
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.slf4j.Logger
import org.springframework.data.relational.core.query.Criteria.from
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Update
import org.springframework.data.relational.core.sql.SqlIdentifier.quoted
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PostMutation(
    private val log: Logger,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : Mutation {

    @Transactional
    @Auth(requires = ["ROLE_USER"])
    suspend fun createPost(data: PostCreateInput): Post {
        log.info("createPost() called with: data = $data")

        val connect = data.author?.connect
        val criteria = from(
            listOfNotNull(
                connect?.id?.let { where("id").`is`(it) },
                connect?.email?.let { where("email").`is`(it) },
            )
        )

        val user = userRepository.findBy(criteria).await()

        return postRepository.save(
            Post(
                title = data.title,
                content = data.content,
                published = data.published ?: false,
                postedAt = LocalDateTime.now(),
                authorId = user?.id ?: ID.Empty
            )
        ).await()
    }

    @Transactional
    @Auth(requires = ["ROLE_USER"])
    suspend fun updatePost(where: PostWhereUniqueInput, data: PostUpdateInput): Post? {
        log.info("updatePost() called with: where = $where, data = $data")
        val criteria = from(where("id").`is`(where.id))

        val update = Update.from(
            buildMap {
                if (data.title != null) {
                    put(quoted("title"), data.title.set)
                }
                if (data.content != null) {
                    put(quoted("content"), data.content.set)
                }
                if (data.published != null) {
                    put(quoted("published"), data.published.set)
                }
            }
        )

        postRepository.update(criteria, update).await()

        return postRepository.findBy(criteria).await()
    }

    @Auth(requires = ["ROLE_USER"])
    suspend fun  deletePost(where: PostWhereUniqueInput): ID? {
        log.info("deletePost() called with: where = $where")

        postRepository.deleteById(where.id).await()

        return where.id
    }
}